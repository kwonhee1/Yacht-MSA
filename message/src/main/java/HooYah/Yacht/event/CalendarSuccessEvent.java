package HooYah.Yacht.event;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class CalendarSuccessEvent extends BasedEvent<CalendarSuccessEvent.Data> {

    public CalendarSuccessEvent(
            Long calendarId, Long userId,
            Long partId, String review, OffsetDateTime startTime
    ) {
        super(calendarId, userId, new Data(partId, review, startTime));
    }

    public Long getPartId() {
        return getData().partId;
    }

    public String getReview() {
        return getData().review;
    }

    public OffsetDateTime getStartTime() {
        return getData().startTime;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Data {
        private Long partId;
        private String review;
        private OffsetDateTime startTime;
    }

}
