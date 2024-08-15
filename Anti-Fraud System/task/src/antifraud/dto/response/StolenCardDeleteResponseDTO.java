package antifraud.dto.response;

import antifraud.data.StolenCard;
import lombok.Data;

@Data
public class StolenCardDeleteResponseDTO {
    private String status;

    public StolenCardDeleteResponseDTO(StolenCard entity) {
        status = "Card " + entity.getNumber() + " successfully removed!";
    }
}
