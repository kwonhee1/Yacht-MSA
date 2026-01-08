package HooYah.Yacht.dto.request;

import HooYah.Yacht.domain.CalendarType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CalendarUpdateRequest {

    private Long partId; // can null, 변경 가능

    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

    private Boolean completed;

    private String content;
    private String review;

    private List<Long> userList = List.of();

    // not use
    @JsonIgnore
    private CalendarType type; // 변경 불가
    @JsonIgnore
    private Long yachtId; // 변경 불가
    @JsonIgnore
    private Boolean byUser = true; // 언제나 true

}

