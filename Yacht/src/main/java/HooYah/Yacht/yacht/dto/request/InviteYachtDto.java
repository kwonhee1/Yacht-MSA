package HooYah.Yacht.yacht.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class InviteYachtDto {

    @NotNull
    private String code;

}
