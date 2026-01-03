package HooYah.Yacht.repair.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RequestRepairDto {

    private Long id;
    private OffsetDateTime date;
    private String content;

}
