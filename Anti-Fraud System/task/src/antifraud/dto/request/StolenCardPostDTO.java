package antifraud.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.LuhnCheck;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StolenCardPostDTO {
    @NotBlank
    @LuhnCheck
    private String number;
}
