package HooYah.Yacht.event;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class CreateEvent extends BasedEvent {
    public CreateEvent(Long id, Long userId) {
        super(id, userId);
    }
}
