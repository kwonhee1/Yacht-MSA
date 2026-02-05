package HooYah.Yacht.dto.repair;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
public class RequestRepairDto {

    private Long id;
    private OffsetDateTime date;
    private String content;

}
