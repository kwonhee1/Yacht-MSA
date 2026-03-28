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
public class UpdateRepairEvent extends CreateEvent {

    private Long yachtId;
    private OffsetDateTime nextRepairDate;

    public UpdateRepairEvent(Long partId, Long yachtId, OffsetDateTime nextRepairDate, Long userId) {
        super(partId, userId);
        this.yachtId = yachtId;
        this.nextRepairDate = nextRepairDate;
    }

}