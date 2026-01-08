package HooYah.Yacht.dto.response;

import HooYah.Yacht.domain.Calendar;
import HooYah.Yacht.domain.CalendarType;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CalendarInfo {

    private Long id;
    private CalendarType type;

    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private boolean completed;
    private boolean byUser;
    private String content;
    private String review;

    private Object yachtInfo;

    private Object partInfo; // can null

    private List userInfoList;

    public static CalendarInfo of(
            Calendar calendar,
            Object partInfo,
            Object yachtInfo,
            List userInfoList
    ) {
        CalendarInfo instance = new CalendarInfo();
        instance.id = calendar.getId();
        instance.type = calendar.getType();
        instance.startDate = calendar.getStartDate();
        instance.endDate = calendar.getEndDate();
        instance.completed = calendar.isCompleted();
        instance.byUser = calendar.isByUser();
        instance.content = calendar.getContent();
        instance.review = calendar.getReview();

        instance.yachtInfo = yachtInfo;

        instance.partInfo = partInfo;
        instance.userInfoList = userInfoList;

        return instance;
    }

    public static List<CalendarInfo> of(
            List<Calendar> calendarList,
            List partInfoList,
            List yachtInfoList,
            List<List<?>> userInfoList
    ) {
        List<CalendarInfo> calendarInfoList = new ArrayList<>();
        for(int i = 0; i <calendarList.size(); i++)
            calendarInfoList.add(CalendarInfo.of(calendarList.get(i), partInfoList.get(i), yachtInfoList.get(i), userInfoList.get(i)));

        return calendarInfoList;
    }

}