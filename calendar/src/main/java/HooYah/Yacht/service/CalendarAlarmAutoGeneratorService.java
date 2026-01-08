package HooYah.Yacht.service;

import HooYah.Yacht.domain.Alarm;
import HooYah.Yacht.repository.AlarmRepository;
import HooYah.Yacht.domain.Calendar;
import HooYah.Yacht.domain.CalendarType;
import HooYah.Yacht.repository.CalendarRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarAlarmAutoGeneratorService {

    private final CalendarRepository calendarRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public void generate(Long partId, Long yachtId, OffsetDateTime nextRepairDate) {
        if(partId == null || nextRepairDate == null)
            return;

        generateCalendar(partId, yachtId, nextRepairDate);
        generateAlarm(partId, yachtId, nextRepairDate);
    }

    private void generateCalendar(Long partId, Long yachtId, OffsetDateTime nextRepairDate) {
        // delete old calendar --> do not delete old calendar
//        List<Calendar> oldCalendarList = calendarRepository.findAllByPartId(partId);
//        oldCalendarList.stream()
//                .filter(c->!c.isByUser())
//                .forEach(c->calendarRepository.delete(c));

        calendarRepository.save(
            Calendar
                .builder()
                .content("Auto Generated")
                .type(CalendarType.PART)
                .startDate(nextRepairDate)
                .endDate(nextRepairDate)
                .partId(partId)
                .yachtId(yachtId)
                .buildByAuto()
        );

    }

    private void generateAlarm(Long partId, Long yachtId, OffsetDateTime nextRepairDate) {
        // 이전 part의 alarm을 삭제함
        alarmRepository.deleteAllByPartId(partId);

        Alarm alarm = Alarm
                .builder()
                .partId(partId)
                .yachtId(yachtId)
                .date(nextRepairDate)
                .build();

        alarmRepository.save(alarm);
    }

}
