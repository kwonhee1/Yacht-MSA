package HooYah.Yacht.calendar;

import HooYah.Yacht.alarm.repository.AlarmRepository;
import HooYah.Yacht.calendar.domain.Calendar;
import HooYah.Yacht.calendar.domain.CalendarType;
import HooYah.Yacht.calendar.domain.CalendarUser;
import HooYah.Yacht.calendar.dto.request.CalendarCreateRequest;
import HooYah.Yacht.calendar.dto.request.CalendarUpdateRequest;
import HooYah.Yacht.calendar.dto.response.CalendarInfo;
import HooYah.Yacht.calendar.repository.CalendarRepository;
import HooYah.Yacht.calendar.service.CalendarService;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.repair.repository.RepairPort;
import HooYah.Yacht.user.domain.User;
import HooYah.Yacht.user.dto.request.RegisterDto;
import HooYah.Yacht.user.repository.UserRepository;
import HooYah.Yacht.user.repository.YachtUserPort;
import HooYah.Yacht.user.service.UserService;
import HooYah.Yacht.yacht.domain.Yacht;
import HooYah.Yacht.yacht.repository.YachtRepository;
import HooYah.Yacht.yacht.service.YachtService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.OffsetDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Disabled
public class CalendarUserTest {

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

    OffsetDateTime now = OffsetDateTime.now();
    @Autowired
    private UserService userService;

    private User newUser;

    @PostConstruct
    public void init(){
        user = userRepository.findById(1L).get();
        yacht = yachtUserPort.findYachtListByUser(user.getId()).get(0);
        part = partRepository.findPartListByYacht(yacht.getId()).get(0);
    }

    @Test
    @Transactional
    public void mainTest(){
        Calendar calendar = createCalendarTest();
        calendar = updateCalendarUserList(calendar);
        deleteCalendar(calendar);
    }

    public Calendar createCalendarTest() {
        CalendarCreateRequest requestDto =  new CalendarCreateRequest();
        requestDto.setPartId(part.getId());
        requestDto.setYachtId(yacht.getId());
        requestDto.setType(CalendarType.PART);
        requestDto.setEndDate(now);
        requestDto.setStartDate(now);
        requestDto.setUserList(List.of(user.getId()));

        CalendarInfo responseDto = calendarService.createCalendar(requestDto, user);

        em.flush();
        em.clear();

        Calendar created = calendarRepository.findById(responseDto.getId()).get();

        Assertions.assertThat(created.getCalendarUsers().stream().map(CalendarUser::getUser).map(User::getId).toList()).contains(user.getId());

        return created;
    }

    public Calendar updateCalendarUserList(Calendar created) {
        // user 한명 만들고 user 를 yacht에 넣는다
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("email123");
        registerDto.setPassword("password");
        registerDto.setName("name");
        newUser = userService.registerWithEmail(registerDto);

        yachtUserPort.addUser(yacht, newUser);

        CalendarUpdateRequest requestDto =  new CalendarUpdateRequest();
        requestDto.setPartId(part.getId());
        requestDto.setYachtId(yacht.getId());
        requestDto.setType(CalendarType.PART);
        requestDto.setEndDate(now);
        requestDto.setStartDate(now);
        requestDto.setUserList(List.of(newUser.getId()));

        CalendarInfo responseDto = calendarService.updateCalendar(created.getId(), requestDto, user);

        em.flush();
        em.clear();

        Calendar updated = calendarRepository.findById(responseDto.getId()).get();

        Assertions.assertThat(updated.getCalendarUsers().stream().map(CalendarUser::getUser).map(User::getId).toList()).contains(newUser.getId());

        return updated;
    }

    public void deleteCalendar(Calendar calendar) {
        calendarRepository.delete(calendar);

        newUser = userRepository.findById(newUser.getId()).get();
    }

}
