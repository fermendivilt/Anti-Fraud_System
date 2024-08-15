package antifraud.dto.response;

import antifraud.enums.AccountStatusAction;
import lombok.Data;

@Data
public class UserAccessPutResponseDTO {
    private String status;

    public UserAccessPutResponseDTO(String username, AccountStatusAction action) {
        String result = "";
        switch (action){
            case LOCK -> result = "locked!";
            case UNLOCK -> result = "unlocked!";
        }
        this.status = "User " + username + " " + result;
    }
}
