package HooYah.Yacht;

import HooYah.Yacht.domain.Calendar;
import HooYah.Yacht.domain.CalendarType;
import HooYah.Yacht.dto.request.CalendarCreateRequest;
import HooYah.Yacht.dto.request.CalendarUpdateRequest;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;

public class TestUtil {

    public static Calendar generateCalendar(
            Long id,
            CalendarType type,
            Long yachtId,
            Long partId,
            OffsetDateTime startDate,
            boolean byUser
    ) {
        Calendar.Builder builder = Calendar.builder()
                .id(id)
                .type(type)
                .yachtId(yachtId)
                .partId(partId)
                .startDate(startDate)
                .endDate(startDate)
                .content("calendar content");
        if (byUser)
            return builder.buildByUser();
        else
            return builder.buildByAuto();
    }

    public static CalendarCreateRequest generateCalendarCreateRequest(
            CalendarType type,
            Long yachtId,
            Long partId,
            boolean isCompleted,
            String review,
            List<Long> userList
    ) {
        CalendarCreateRequest dto = new CalendarCreateRequest();
        dto.setType(type);
        dto.setYachtId(yachtId);
        dto.setPartId(partId);
        dto.setStartDate(OffsetDateTime.now());
        dto.setEndDate(OffsetDateTime.now());
        dto.setUserList(userList);
        dto.setContent("test content");
        dto.setReview(review);
        dto.setCompleted(isCompleted);
        return dto;
    }

    public static CalendarUpdateRequest generateCalendarUpdateRequest(
            Long partId,

            OffsetDateTime startDate,
            OffsetDateTime endDate,

            boolean isCompleted,

            String content,
            String review,

            List<Long> userList
    ) {
        CalendarUpdateRequest dto = new CalendarUpdateRequest();
        dto.setPartId(partId);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setContent(content);
        dto.setReview(review);
        dto.setCompleted(isCompleted);
        dto.setUserList(userList);
        return dto;
    }

}

