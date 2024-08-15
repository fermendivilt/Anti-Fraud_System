package antifraud.dto.response;

import antifraud.data.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteResponseDTO {
    private String username;
    private final String status = "Deleted successfully!";

    public UserDeleteResponseDTO(AppUser user) {
        this.username = user.getUsername();
    }
}
