package HooYah.Yacht.event;

import java.time.OffsetDateTime;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class CalendarCompleteEvent extends BasedEvent {

    private Long partId;
    private OffsetDateTime completeTime;
    private String content;

    public CalendarCompleteEvent(
            Long id, Long userId,
            Long partId,
            OffsetDateTime completeTime, String content
    ) {
        super(id, userId);
        this.partId = partId;
        this.completeTime = completeTime;
        this.content = content;
    }

    // getter
    public OffsetDateTime getCompleteTimeValue() {
        return completeTime;
    }
    public Long getPartIdValue() {
        return partId;
    }
    public String getContent() {
        return content;
    }

    // string setter
    public void setCompleteTime(String completeTime) {
        this.completeTime = OffsetDateTime.parse(completeTime);
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setPartId(String partId) {
        this.partId = Long.parseLong(partId);
    }
    // string getter
    public String getCompleteTime() {
        return completeTime.toString();
    }
    public String getPartId() {
        return partId.toString();
    }

}
