package HooYah.Yacht.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
@Setter
public class SuccessResponse {
    private int status;
    private String message;
    private Object response;

    public SuccessResponse(HttpStatus status, String message, Object response) {
        this.status = status.value();
        this.message = message;
        this.response = response;
    }
}
