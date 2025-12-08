package HooYah.Yacht.calendar;

import HooYah.Yacht.alarm.domain.Alarm;
import HooYah.Yacht.alarm.repository.AlarmRepository;
import HooYah.Yacht.calendar.domain.Calendar;
import HooYah.Yacht.calendar.domain.CalendarType;
import HooYah.Yacht.calendar.dto.request.CalendarCreateRequest;
import HooYah.Yacht.calendar.dto.request.CalendarUpdateRequest;
import HooYah.Yacht.calendar.dto.response.CalendarInfo;
import HooYah.Yacht.calendar.repository.CalendarRepository;
import HooYah.Yacht.calendar.service.CalendarService;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.repair.domain.Repair;
import HooYah.Yacht.repair.repository.RepairPort;
import HooYah.Yacht.user.domain.User;
import HooYah.Yacht.user.repository.UserRepository;
import HooYah.Yacht.user.repository.YachtUserPort;
import HooYah.Yacht.user.service.UserService;
import HooYah.Yacht.yacht.domain.Yacht;
import HooYah.Yacht.yacht.repository.YachtRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Disabled
public class CalendarTest {

    @Autowired
    private CalendarService calendarService;
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private YachtUserPort yachtUserPort;
    @Autowired
    private PartRepository partRepository;

    private User user;
    private Yacht yacht;
    private Part part;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private RepairPort repairPort;

    OffsetDateTime now = OffsetDateTime.now();

    @PostConstruct
    public void init(){
        user = userRepository.findById(1L).get();
        yacht = yachtUserPort.findYachtListByUser(user.getId()).get(0);
        part = partRepository.findPartListByYacht(yacht.getId()).get(0);
    }

    @Test
    @DisplayName("type != part인 calendar 생성 수정 삭제")
    @Transactional
    public void NotPartCalendarTest() {
        Calendar created = createNotPartCalendar();
        log.info("created end");
        created = updateNotPartCalendar(created);
        log.info("update end");
        created = setCompletedTrueNotPartCalendar(created);
        log.info("completed true end");
        deleteNotPartCalendar(created);
    }

    private Calendar createNotPartCalendar() {
        CalendarCreateRequest createDto = new CalendarCreateRequest();
        createDto.setYachtId(yacht.getId());
        createDto.setContent("content");
        createDto.setCompleted(false);
        createDto.setStartDate(OffsetDateTime.now());
        createDto.setEndDate(OffsetDateTime.now());
        createDto.setType(CalendarType.SAILING);

        CalendarInfo createdDto = calendarService.createCalendar(createDto, user);
        Calendar createdCalendar = calendarRepository.findById(createdDto.getId()).get();

        em.flush();

        Assertions.assertThat(createdCalendar).isNotNull();

        Assertions.assertThat(calendarRepository.findByYacht(yacht)).contains(createdCalendar);
        return createdCalendar;
    }

    private Calendar updateNotPartCalendar(Calendar calendar) {
        CalendarUpdateRequest updateDto = new CalendarUpdateRequest();
        updateDto.setStartDate(OffsetDateTime.now());
        updateDto.setEndDate(OffsetDateTime.now());
        updateDto.setContent("updated contend");
        updateDto.setType(CalendarType.SAILING);

        CalendarInfo updatedDto = calendarService.updateCalendar(calendar.getId(), updateDto, user);
        Calendar updated = calendarRepository.findById(updatedDto.getId()).get();

        Assertions.assertThat(updated.isByUser()).isTrue();
        Assertions.assertThat(updated.getContent()).isEqualTo("updated contend");

        return updated;
    }

    private Calendar setCompletedTrueNotPartCalendar(Calendar calendar) {
        CalendarUpdateRequest updateDto = new CalendarUpdateRequest();
        updateDto.setStartDate(OffsetDateTime.now());
        updateDto.setEndDate(OffsetDateTime.now());
        updateDto.setCompleted(true);
        updateDto.setType(CalendarType.SAILING);

        CalendarInfo updatedDto = calendarService.updateCalendar(calendar.getId(), updateDto, user);
        Calendar updated = calendarRepository.findById(updatedDto.getId()).get();

        Assertions.assertThat(updated.isCompleted()).isTrue();

        return updated;
    }

    private void deleteNotPartCalendar(Calendar calendar) {
        calendarService.deleteCalendar(calendar.getId(), user);
    }

    @Test
    @Transactional
    public void partTypeCalendarTest() {
        Calendar created = createPartTypeCalendar();
        log.info("created end");
        created = updatePartTypeCalendar(created);
        log.info("update end");
        created = setCompletedTruePartTypeCalendar(created);
        log.info("completed true end");
        deletePartTypeCalendarTest(created);
    }

    private Calendar createPartTypeCalendar() {
        CalendarCreateRequest createDto = new CalendarCreateRequest();
        createDto.setYachtId(yacht.getId());
        createDto.setPartId(part.getId());
        createDto.setContent("content");
        createDto.setCompleted(false);
        createDto.setStartDate(now);
        createDto.setEndDate(now);
        createDto.setType(CalendarType.PART);

        CalendarInfo createdDto = calendarService.createCalendar(createDto, user);
        Calendar created = calendarRepository.findById(createdDto.getId()).get();

        Assertions.assertThat(created).isNotNull();

        // 이전 Calendar를 삭제 할까요?
        // Assertions.assertThat(calendarRepository.findByYacht(yacht).getLast()).isEqualTo(created);

        return created;
    }

    private Calendar updatePartTypeCalendar(Calendar calendar) {
        CalendarUpdateRequest updateDto = new CalendarUpdateRequest();
        updateDto.setStartDate(now);
        updateDto.setEndDate(now);
        updateDto.setContent("updated contend");
        updateDto.setPartId(part.getId());
        updateDto.setType(CalendarType.PART);

        CalendarInfo createdDto = calendarService.updateCalendar(calendar.getId(), updateDto, user);
        Calendar updated = calendarRepository.findById(createdDto.getId()).get();

        Assertions.assertThat(updated.isByUser()).isTrue();

        return updated;
    }

    private Calendar setCompletedTruePartTypeCalendar(Calendar calendar) {
        CalendarUpdateRequest updateDto = new CalendarUpdateRequest();
        updateDto.setStartDate(now);
        updateDto.setEndDate(now);
        updateDto.setPartId(part.getId());
        updateDto.setType(CalendarType.PART);
        updateDto.setCompleted(true);

        CalendarInfo createdDto = calendarService.updateCalendar(calendar.getId(), updateDto, user);
        Calendar updated = calendarRepository.findById(createdDto.getId()).get();

        // part의 repair가 생성되었는지 확인
        Optional<Repair> lastRepair = repairPort.findLastRepair(part);
        Assertions.assertThat(lastRepair.isPresent()).isTrue();
        Assertions.assertThat(lastRepair.get().getRepairDate().toLocalDate()).isEqualTo(now.toLocalDate());

        // 해당 part의 알림이 새로 생성되었는지 확인
        List<Alarm> alarms = alarmRepository.findAllByYachtIds(List.of(yacht.getId()), OffsetDateTime.now());
        Assertions.assertThat(alarms.getLast().getDate().toLocalDate()).isEqualTo(part.nextRepairDate(now).toLocalDate());

        // 해당 part의 calendar가 새로 생성되었는지 확인 -> is user == false이고, start date가 part.nextDate와 같음
        List<Calendar> calendars = calendarRepository.findAllByPartId(part.getId());
        org.junit.jupiter.api.Assertions.assertAll(
                ()->Assertions.assertThat(calendars.getLast().getStartDate().toLocalDate()).isEqualTo(part.nextRepairDate(now).toLocalDate()),
                ()->Assertions.assertThat(calendars.getLast().isByUser()).isFalse()
        );

        // 이전 calendar에 대해서 어찌 할 까요?

        return updated;
    }

    private void deletePartTypeCalendarTest(Calendar calendar) {
        calendarService.deleteCalendar(calendar.getId(), user);

        // 켈린더를 삭제해도 repair을 같이 삭제 하지 않기 때문에 알림, 다른 켈린더에 변화가 생기지 않는다
    }
    // 이전 값이 있을 때와 없을 때
    // 이전 값을 지울지 여부 부터 결정 필요!

}
