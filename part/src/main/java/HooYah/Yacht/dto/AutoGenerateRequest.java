package HooYah.Yacht.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoGenerateRequest {

    private Long partId;

    private Long yachtId;

    private OffsetDateTime nextRepairDate;

}
