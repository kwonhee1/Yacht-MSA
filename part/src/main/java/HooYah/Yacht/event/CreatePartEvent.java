package HooYah.Yacht.event;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreatePartEvent extends CreateEvent {

    private Long yachtId;
    private OffsetDateTime lastRepairDate;

    public CreatePartEvent(Long partId, Long yachtId, OffsetDateTime lastRepairDate, Long userId) {
        super(partId, userId);
        this.yachtId = yachtId;
        this.lastRepairDate = lastRepairDate;
    }

}
