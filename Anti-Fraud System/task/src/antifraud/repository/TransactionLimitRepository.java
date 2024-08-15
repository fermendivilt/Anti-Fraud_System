package antifraud.repository;

import antifraud.data.TransactionLimit;
import antifraud.enums.TransactionEvaluation;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransactionLimitRepository extends CrudRepository<TransactionLimit, Long> {
    Optional<TransactionLimit> findByEvaluation(TransactionEvaluation evaluation);
    boolean existsByEvaluation(TransactionEvaluation evaluation);
}
