package HooYah.Yacht.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Getter
public class DeletedEvent extends BasedEventData {

    public DeletedEvent(Long id, Long userId) {
        super(id, userId);
    }

}
