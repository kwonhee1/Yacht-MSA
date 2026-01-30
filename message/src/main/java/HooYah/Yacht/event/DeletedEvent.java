package HooYah.Yacht.event;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class DeletedEvent extends BasedEvent<DeletedEvent.Data> {

    public DeletedEvent(Long id, Long userId) {
        super(id, userId, new Data());
    }

    @NoArgsConstructor
    @ToString
    public static class Data {
    }

}
