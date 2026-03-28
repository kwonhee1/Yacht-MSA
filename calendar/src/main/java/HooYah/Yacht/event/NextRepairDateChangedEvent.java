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
public class NextRepairDateChangedEvent extends BasedEvent {

    private Long yachtId;
    private OffsetDateTime nextRepairDate;

    public NextRepairDateChangedEvent(Long partId, Long userId, Long yachtId, OffsetDateTime nextRepairDate) {
        super(partId, userId);
        this.yachtId = yachtId;
        this.nextRepairDate = nextRepairDate;
    }

}