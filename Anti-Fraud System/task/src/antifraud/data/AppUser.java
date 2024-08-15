package antifraud.data;

import antifraud.dto.request.UserPostDTO;
import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String password;
    private String authority;
    private boolean accountNonLocked = false;

    public AppUser(UserPostDTO dto) {
        this.name = dto.getName();
        this.username = dto.getUsername();
        this.password = dto.getPassword();
    }
}
