package antifraud.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static antifraud.config.Constants.ipValidRegEx;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousIPPostDTO {
    @NotBlank
    @Pattern(regexp = ipValidRegEx,
            message = "Invalid IP address")
    private String ip;
}
