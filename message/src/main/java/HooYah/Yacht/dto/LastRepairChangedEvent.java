package HooYah.Yacht.dto;

import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Getter
public class LastRepairChangedEvent extends BasedEventData {

    private Long yachtId;
    private OffsetDateTime nextRepairDate;

    public LastRepairChangedEvent(
            Long partId, Long userId,
            Long yachtId,
            OffsetDateTime nextRepairDate
    ) {
        super(partId, userId);
        this.yachtId = yachtId;
        this.nextRepairDate = nextRepairDate;
    }
}
