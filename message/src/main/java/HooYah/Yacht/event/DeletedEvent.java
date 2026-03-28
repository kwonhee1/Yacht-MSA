package HooYah.Yacht.event;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class DeletedEvent extends BasedEvent {
    public DeletedEvent(Long id, Long userId) {
        super(id, userId);
    }
}
