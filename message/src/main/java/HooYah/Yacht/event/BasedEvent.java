package HooYah.Yacht.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public abstract class BasedEvent {

    // when
    private OffsetDateTime sendTime = OffsetDateTime.now();

    // what
    private Long id;

    // who
    private Long userId;

    public BasedEvent(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    // string setter
    public void setSendTime(String sendTime) {
        this.sendTime = OffsetDateTime.parse(sendTime);
    }
    public void setId(String id) {
        this.id = Long.parseLong(id);
    }
    public void setUserId(String userId) {
        this.userId = Long.parseLong(userId);
    }

    // string getter
    public String getSendTime() {
        return sendTime.toString();
    }
    public String getUserId() {
        return userId.toString();
    }
    public String getId() {
        return id.toString();
    }

    // getter
    @JsonIgnore
    public OffsetDateTime getSendTimeValue() {
        return sendTime;
    }
    @JsonIgnore
    public Long getUserIdValue() {
        return userId;
    }
    @JsonIgnore
    public Long getIdValue() {
        return id;
    }

}
