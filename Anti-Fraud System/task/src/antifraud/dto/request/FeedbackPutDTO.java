package antifraud.dto.request;

import antifraud.enums.TransactionEvaluation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackPutDTO {
    @Min(1)
    @NotNull
    private Long transactionId;

    @NotNull
    private TransactionEvaluation feedback;
}
