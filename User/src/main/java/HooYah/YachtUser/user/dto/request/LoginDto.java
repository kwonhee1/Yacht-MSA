package HooYah.YachtUser.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginDto {

    @NotEmpty
    private String email;
    @NotEmpty
    private String password;

}
