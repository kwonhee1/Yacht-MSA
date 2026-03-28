package HooYah.Yacht.yacht.event;

import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.yacht.dto.request.PartRequest;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class YachtCreateEvent extends BasedEvent {

    private List<PartRequest> partList;

    public YachtCreateEvent(Long yachtId, Long userId, List<PartRequest> partList) {
        super(yachtId, userId);
        this.partList = partList;
    }
}
