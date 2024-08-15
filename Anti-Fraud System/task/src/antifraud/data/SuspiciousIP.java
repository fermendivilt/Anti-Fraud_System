package antifraud.data;

import antifraud.dto.request.SuspiciousIPPostDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SuspiciousIP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ip;

    public SuspiciousIP(SuspiciousIPPostDTO dto) {
        ip = dto.getIp();
    }
}
