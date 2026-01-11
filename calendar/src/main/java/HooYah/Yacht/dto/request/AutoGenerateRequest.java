package HooYah.Yacht.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AutoGenerateRequest {

    @NotNull
    private Long partId;

    @NotNull
    private Long yachtId;

    @NotNull
    private OffsetDateTime nextRepairDate;

}

