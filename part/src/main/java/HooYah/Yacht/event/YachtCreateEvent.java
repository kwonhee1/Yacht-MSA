package HooYah.Yacht.event;

import HooYah.Yacht.dto.part.AddPartDto;
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

    private List<AddPartDto> partList;

    public YachtCreateEvent(Long yachtId, Long userId, List<AddPartDto> partList) {
        super(yachtId, userId);
        this.partList = partList;
    }
}
