package antifraud.data;

import antifraud.dto.request.TransactionPostDTO;
import antifraud.enums.RegionCode;
import antifraud.enums.TransactionEvaluation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long amount;
    private String ip;
    private String number;
    @Enumerated(EnumType.STRING)
    private RegionCode region;
    private LocalDateTime date;
    private TransactionEvaluation result;
    private TransactionEvaluation feedback;

    public Transaction(TransactionPostDTO dto, TransactionEvaluation result) {
        amount = dto.getAmount();
        ip = dto.getIp();
        number = dto.getNumber();
        region = dto.getRegion();
        date = dto.getDate();
        this.result = result;
        feedback = null;
    }
}
