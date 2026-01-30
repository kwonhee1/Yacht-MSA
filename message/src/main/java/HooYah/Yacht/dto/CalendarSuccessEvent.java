package HooYah.Yacht.dto;

import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Getter
public class CalendarSuccessEvent extends BasedEventData {

    private Long partId;
    private String review;
    private OffsetDateTime startTime;

    // converter 사용 고려, publisher에서 등록할 때 converter를 등록한다
    public CalendarSuccessEvent(
            Long calendarId, Long userId,
            Long partId, String review, OffsetDateTime startTime
    ) {
        super(calendarId, userId);
        this.partId = partId;
        this.review = review;
        this.startTime = startTime;
    }

}
