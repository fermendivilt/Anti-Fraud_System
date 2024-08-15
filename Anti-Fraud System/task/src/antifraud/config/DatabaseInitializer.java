package antifraud.config;

import antifraud.data.TransactionLimit;
import antifraud.enums.TransactionEvaluation;
import antifraud.repository.TransactionLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Autowired
    private TransactionLimitRepository transactionLimitRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!transactionLimitRepository.existsByEvaluation(TransactionEvaluation.ALLOWED)) {
            TransactionLimit setting = new TransactionLimit();
            setting.setEvaluation(TransactionEvaluation.ALLOWED);
            setting.setTransactionLimit(200L);
            transactionLimitRepository.save(setting);
        }

        if (!transactionLimitRepository.existsByEvaluation(TransactionEvaluation.MANUAL_PROCESSING)) {
            TransactionLimit setting = new TransactionLimit();
            setting.setEvaluation(TransactionEvaluation.MANUAL_PROCESSING);
            setting.setTransactionLimit(1500L);
            transactionLimitRepository.save(setting);
        }
    }
}
