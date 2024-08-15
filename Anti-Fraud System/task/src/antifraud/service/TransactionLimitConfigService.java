package antifraud.service;

import antifraud.data.TransactionLimit;
import antifraud.enums.TransactionEvaluation;
import antifraud.repository.TransactionLimitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static antifraud.enums.TransactionEvaluation.*;

@Service
public class TransactionLimitConfigService {

    @Autowired
    private TransactionLimitRepository repository;

    @Cacheable("transactionLimits")
    public TransactionLimit getTransactionLimit(TransactionEvaluation evaluation) {
        if(evaluation.equals(PROHIBITED)) throw new InvalidParameterException("No entity for prohibited transactions");
        return repository.findByEvaluation(evaluation).orElseThrow(EntityNotFoundException::new);
    }

    @CacheEvict(value = "transactionLimits", allEntries = true)
    public void updateTransactionLimit(TransactionEvaluation evaluation,
                                       TransactionEvaluation feedback,
                                       Long transactionValue)
                                        throws IllegalArgumentException {

        if(evaluation.equals(feedback))
            throw new IllegalArgumentException("Both TransactionEvaluation must be different.");

        BiConsumer<Boolean, List<TransactionLimit>> assignNewLimits = (isIncreasing, limits) -> {
            for(TransactionLimit limit : limits)
                limit.setTransactionLimit(newLimit(isIncreasing, limit.getTransactionLimit(), transactionValue));
        };

        List<TransactionLimit> transactionLimits =
                Arrays.asList(getTransactionLimit(ALLOWED), getTransactionLimit(MANUAL_PROCESSING));

        switch (evaluation) {
            case ALLOWED:
                if(feedback.equals(MANUAL_PROCESSING))
                    assignNewLimits.accept(false,
                            Collections.singletonList(transactionLimits.get(0)));
                else
                    assignNewLimits.accept(false,
                            Arrays.asList(transactionLimits.get(0), transactionLimits.get(1)));
                break;
            case MANUAL_PROCESSING:
                if(feedback.equals(ALLOWED))
                    assignNewLimits.accept(true,
                            Collections.singletonList(transactionLimits.get(0)));
                else
                    assignNewLimits.accept(false,
                            Collections.singletonList(transactionLimits.get(1)));
                break;
            case PROHIBITED:
                if(feedback.equals(ALLOWED))
                    assignNewLimits.accept(true,
                            Arrays.asList(transactionLimits.get(0), transactionLimits.get(1)));
                else
                    assignNewLimits.accept(true,
                            Collections.singletonList(transactionLimits.get(1)));
                break;
        }

        repository.saveAll(transactionLimits);
    }

    protected Long newLimit(boolean isIncreasing, Long currentLimit, Long transactionValue) {
        if(isIncreasing)
            return (long) Math.ceil(0.8 * currentLimit + 0.2 * transactionValue);
        else
            return (long) Math.ceil(0.8 * currentLimit - 0.2 * transactionValue);
    }
}
