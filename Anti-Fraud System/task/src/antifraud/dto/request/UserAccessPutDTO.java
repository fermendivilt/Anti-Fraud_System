package antifraud.dto.request;

import antifraud.enums.AccountStatusAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccessPutDTO {
    @NotBlank
    private String username;
    @NotNull
    private AccountStatusAction operation;
}
