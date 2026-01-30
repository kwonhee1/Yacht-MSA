package HooYah.Yacht.event;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class LastRepairChangedEvent extends BasedEvent<LastRepairChangedEvent.Data> {

    public LastRepairChangedEvent(
            Long partId, Long userId,
            Long yachtId,
            OffsetDateTime nextRepairDate
    ) {
        super(partId, userId, new Data(yachtId, nextRepairDate));
    }

    public Long getYachtId() {
        return getData().yachtId;
    }

    public OffsetDateTime getNextRepairDate() {
        return getData().nextRepairDate;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Data {
        private Long yachtId;
        private OffsetDateTime nextRepairDate;
    }
}
