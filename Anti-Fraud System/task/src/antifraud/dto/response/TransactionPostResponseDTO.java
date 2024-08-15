package antifraud.dto.response;

import antifraud.enums.TransactionEvaluation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPostResponseDTO {
    TransactionEvaluation result;
    String info;
}
