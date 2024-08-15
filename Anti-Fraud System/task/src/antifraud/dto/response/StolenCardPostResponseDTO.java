package antifraud.dto.response;

import antifraud.data.StolenCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StolenCardPostResponseDTO {
    private Long id;
    private String number;

    public StolenCardPostResponseDTO(StolenCard result) {
        id = result.getId();
        number = result.getNumber();
    }
}
