package antifraud.dto.response;

import antifraud.data.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPostResponseDTO {
    private Long id;
    private String name;
    private String username;
    private String role;

    public UserPostResponseDTO(AppUser result) {
        id = result.getId();
        name = result.getName();
        username = result.getUsername();
        role = result.getAuthority().replace("ROLE_", "");
    }
}
