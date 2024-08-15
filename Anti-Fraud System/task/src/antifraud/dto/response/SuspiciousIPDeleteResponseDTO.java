package antifraud.dto.response;

import antifraud.data.SuspiciousIP;
import lombok.Data;

@Data
public class SuspiciousIPDeleteResponseDTO {
    private String status;

    public SuspiciousIPDeleteResponseDTO(SuspiciousIP entity) {
        status = "IP " + entity.getIp() + " successfully removed!";
    }
}
