package antifraud.dto.response;

import antifraud.data.SuspiciousIP;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousIPPostResponseDTO {
    private Long id;
    private String ip;

    public SuspiciousIPPostResponseDTO(SuspiciousIP suspiciousIP) {
        id = suspiciousIP.getId();
        ip = suspiciousIP.getIp();
    }
}
