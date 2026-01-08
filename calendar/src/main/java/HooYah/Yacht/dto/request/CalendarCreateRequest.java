package HooYah.Yacht.dto.request;

import HooYah.Yacht.domain.CalendarType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CalendarCreateRequest {

    @NotNull
    private CalendarType type;

    private Long partId;

    @NotNull
    private Long yachtId;

    @NotNull
    private OffsetDateTime startDate = OffsetDateTime.now();

    @NotNull
    private OffsetDateTime endDate = OffsetDateTime.now();

    private Boolean completed = false;

    private Boolean byUser = true;

    private String content;

    private String review;

    private List<Long> userList = new ArrayList<>();

}

