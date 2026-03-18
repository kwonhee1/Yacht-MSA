package HooYah.Yacht.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlarmTokenDto {
    private Long userId;
    private String token;

    public AlarmTokenDto(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
