package HooYah.Yacht.yacht.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateYachtDto {

    @NotNull
    @Valid
    private YachtInfo yacht;

    @Valid
    private List<PartRequest> partList;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class YachtInfo {
        @NotEmpty
        private String name;
        private String nickName;
    }

}
