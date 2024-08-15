package antifraud.dto.response;

import antifraud.data.Transaction;
import antifraud.enums.RegionCode;
import antifraud.enums.TransactionEvaluation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionGetResponseDTO {
    private Long transactionId;
    private Long amount;
    private String ip;
    private String number;
    private RegionCode region;
    private LocalDateTime date;
    private TransactionEvaluation result;
    private String feedback;

    public TransactionGetResponseDTO(Transaction transaction) {
        transactionId = transaction.getId();
        amount = transaction.getAmount();
        ip = transaction.getIp();
        number = transaction.getNumber();
        region = transaction.getRegion();
        date = transaction.getDate();
        result = transaction.getResult();
        feedback = Objects.requireNonNullElse(transaction.getFeedback(), "").toString();
    }
}
