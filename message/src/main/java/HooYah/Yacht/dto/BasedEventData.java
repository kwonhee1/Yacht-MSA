package HooYah.Yacht.dto;

import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Getter
@ToString
public class BasedEventData {

    // when
    private OffsetDateTime sendTime = OffsetDateTime.now();

    // what
    private Long id;

    // who
    private Long userId;

    public BasedEventData(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }
}
