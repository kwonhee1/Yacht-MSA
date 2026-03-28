package HooYah.Yacht.yacht.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PartRequest {
    private Long yachtId;
    @NotEmpty
    private String name;
    private String manufacturer;
    private String model;
    @NotNull
    private Long interval;
    private OffsetDateTime lastRepair;
}
