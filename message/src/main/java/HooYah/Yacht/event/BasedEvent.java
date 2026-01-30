package HooYah.Yacht.event;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class BasedEvent<T> {

    // when
    private OffsetDateTime sendTime = OffsetDateTime.now();

    // what
    private Long id;

    // who
    private Long userId;

    private T data;

    public BasedEvent(Long id, Long userId, T data) {
        this.id = id;
        this.userId = userId;
        this.data = data;
    }
}
