package antifraud.dto.request;

import antifraud.enums.RegionCode;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.LuhnCheck;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static antifraud.config.Constants.ipValidRegEx;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPostDTO {
    @NotNull
    @Min(1)
    private Long amount;
    @NotBlank
    @Pattern(regexp = ipValidRegEx)
    private String ip;
    @NotBlank
    @LuhnCheck
    private String number;
    @NotNull
    private RegionCode region;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;
}
