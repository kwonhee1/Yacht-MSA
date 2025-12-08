package HooYah.Yacht.calendar;

import HooYah.Yacht.alarm.domain.Alarm;
import HooYah.Yacht.alarm.repository.AlarmRepository;
import HooYah.Yacht.calendar.domain.Calendar;
import HooYah.Yacht.calendar.repository.CalendarRepository;
import HooYah.Yacht.calendar.service.CalendarAlarmAutoGeneratorService;
import HooYah.Yacht.calendar.service.CalendarService;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.part.dto.request.AddPartDto;
import HooYah.Yacht.part.dto.request.UpdatePartDto;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.part.service.PartService;
import HooYah.Yacht.repair.repository.RepairPort;
import HooYah.Yacht.repair.service.RepairService;
import HooYah.Yacht.user.domain.User;
import HooYah.Yacht.user.repository.UserRepository;
import HooYah.Yacht.user.repository.YachtUserPort;
import HooYah.Yacht.yacht.domain.Yacht;
import HooYah.Yacht.yacht.repository.YachtRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
public class CalendarAlarmAutoGenerateServiceTest {

    @Autowired
    private CalendarService calendarService;
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private YachtRepository yachtRepository;
    @Autowired
    private YachtUserPort yachtUserPort;
    @Autowired
    private PartRepository partRepository;
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private RepairPort repairPort;
    @Autowired
    private PartService partService;
    @Autowired
    private RepairService repairService;

    private User user;
    private Yacht yacht;
    private Part part;

    OffsetDateTime now = OffsetDateTime.now();

    @PostConstruct
    public void init(){
        user = userRepository.findById(1L).get();
        yacht = yachtUserPort.findYachtListByUser(user.getId()).get(0);
        part = partRepository.findPartListByYacht(yacht.getId()).get(0);
    }

    @Test
    @Transactional
    @DisplayName("새로운 부품을 만들고 repair를 추가했을 때 calendar, alarm이 생성되어야 함")
    public void autoGenerateTest() {
        Part newPart = partService.addPart(yacht.getId(), generateNewPartDto(), user);
        repairService.addRepair(newPart.getId(), "정비 내용", now, user);

        em.flush();
        em.clear();

        newPart = partRepository.findById(newPart.getId()).get();

        // 생성된 part에 대해 알림과, calendar가 생성되었는지 확인
        Assertions.assertThat(newPart.getAlarms()).isNotEmpty();
        Assertions.assertThat(newPart.getCalendars()).isNotEmpty();
    }

    private AddPartDto generateNewPartDto() {
        AddPartDto addPartDto = new AddPartDto();
        addPartDto.setYachtId(yacht.getId());
        addPartDto.setInterval(1L);
        addPartDto.setName("부품 이름");
        return addPartDto;
    }

    @Test
    @Transactional
    @DisplayName("part interval을 update했을 떄 alarm, calendar의 값이 update되어야 함")
    public void updatePartIntervalTest() {
        // 부품을 추가한다
        Part newPart = partService.addPart(yacht.getId(), generateNewPartDto(), user);
        repairService.addRepair(newPart.getId(), "정비 내용", now, user);
        // 부품의 interval을 수정한다
        UpdatePartDto updatePartDto = new UpdatePartDto();
        updatePartDto.setInterval(2L);
        updatePartDto.setId(newPart.getId());
        partService.updatePart(updatePartDto, user);

        // 확인한다
        em.flush();
        em.clear();

        newPart = partRepository.findById(newPart.getId()).get();

        // 생성된 part에 대해 알림과, calendar가 생성되었는지 확인
        LocalDate nextRepairDate = newPart.nextRepairDate(now).toLocalDate();

        // 이전 알림이 삭제되었는지 확인
        Assertions.assertThat(newPart.getAlarms().size()).isEqualTo(1);
        // 다음 정비일에 알림이 생성되었는지 확인
        Alarm alarm = newPart.getAlarms().getFirst();
        Assertions.assertThat(alarm.getDate().toLocalDate()).isEqualTo(nextRepairDate);

        // 켈린더의 날짜가 수정되었는지 확인
        Calendar calendar = newPart.getCalendars().getLast();
        org.junit.jupiter.api.Assertions.assertAll(
                ()->Assertions.assertThat(calendar.isByUser()).isFalse(),
                ()->Assertions.assertThat(calendar.getStartDate().toLocalDate()).isEqualTo(nextRepairDate)
        );
    }
    // todo : 이전 켈린더가 isUser true일때 작동확인
    // todo : 이전 켈린더가 여러개인 경우 동작 확인

    @Test
    @Transactional
    @DisplayName("repair 값을 추가 / 수정 / 삭제하면 이전 alarm, calender 값이 수정되어야 함")
    public void updateRepairTest() {

    }

}
