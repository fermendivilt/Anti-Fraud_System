package antifraud.data;

import antifraud.enums.TransactionEvaluation;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class TransactionLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TransactionEvaluation evaluation;
    private Long transactionLimit;

    public TransactionLimit(TransactionEvaluation evaluation, Long transactionLimit) {
        this.evaluation = evaluation;
        this.transactionLimit = transactionLimit;
    }
}
