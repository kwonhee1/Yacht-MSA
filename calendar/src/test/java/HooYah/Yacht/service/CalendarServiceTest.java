package HooYah.Yacht.service;

import static HooYah.Yacht.TestUtil.*;

import HooYah.Yacht.domain.Calendar;
import HooYah.Yacht.domain.CalendarType;
import HooYah.Yacht.dto.request.CalendarCreateRequest;
import HooYah.Yacht.dto.request.CalendarUpdateRequest;
import HooYah.Yacht.dto.response.CalendarInfo;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.repository.CalendarRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import java.util.ArrayList;

public class CalendarServiceTest {

    private CalendarService calendarService;

    private CalendarRepository mockCalendarRepository;
    private AskService mockAskService;
    private WebClient mockWebClient;

    static Long userId = 1L;
    static Long yachtId = 1L;
    static Long partId = 1L;

    @BeforeEach
    public void init() {
        mockCalendarRepository = Mockito.mock(CalendarRepository.class);
        mockAskService = Mockito.mock(AskService.class);
        mockWebClient = Mockito.mock(WebClient.class);

        calendarService = new CalendarService(
                mockCalendarRepository,
                mockAskService,
                mockWebClient
        );

        Mockito.lenient().when(mockAskService.validateYachtUser(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Map.of("yacht", "yacht"));
        Mockito.lenient().when(mockAskService.validatePart(Mockito.anyLong()))
                .thenReturn(Map.of("part", "part"));
        Mockito.lenient().when(mockAskService.getUserInfoList(Mockito.anyList()))
                .thenAnswer(invocation -> {
                    Map userInfo = Map.of("user", "user");
                    List answer = new ArrayList();

                    int count = ((List)invocation.getArgument(0)).size();
                    for(int i = 0; i < count; i++)
                        answer.add(userInfo);

                    return answer;
                });
    }

    // test : createdCalendar.isUser == true, if(partType) partId != null, check completed, delete old calendars (추후 domain 로직으로 분리해서 다시 Test)
    @ParameterizedTest
    @MethodSource
    public void createCalendarByUserTest(CalendarCreateRequest dto, Class expectedException) {
        // mock : calendar.findByPartId :: return List.empty()
        Mockito.when(mockCalendarRepository.findByPartId(Mockito.anyLong()))
                .thenReturn(List.of());
        // mock: validateYachtUser, validatePart, getUserInfoList

        if(expectedException != null) {
            org.junit.jupiter.api.Assertions.assertThrows(expectedException,
                    ()->calendarService.createCalendarByUser(dto, userId)
            );
        }else {
            CalendarInfo result = calendarService.createCalendarByUser(dto, userId);

            org.junit.jupiter.api.Assertions.assertAll(
                    () -> Assertions.assertThat(result.isByUser()).isTrue(),
                    () -> Assertions.assertThat(result.getUserInfoList().size()).isEqualTo(dto.getUserList().size()),
                    () -> Mockito.verify(mockWebClient, Mockito.times(dto.getCompleted()?1:0)) // completed == true 이면 repair domain에게 요청하기
                            .webClient(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.notNull())
            );
        }
    }

    public static Stream<Arguments> createCalendarByUserTest() {
        return Stream.of(
                Arguments.of(generateCalendarCreateRequest(CalendarType.PART, yachtId, partId, true, "review", List.of(1L,2L,3L,4L)), null),
                Arguments.of(generateCalendarCreateRequest(CalendarType.SAILING, yachtId, null, false, "review", List.of(1L,2L,3L,4L)), null),
                Arguments.of(generateCalendarCreateRequest(CalendarType.PART, yachtId, null, false, "review", List.of(1L,2L,3L,4L)), CustomException.class), // conflict
                Arguments.of(generateCalendarCreateRequest(CalendarType.PART, yachtId, partId, true, null, List.of(1L,2L,3L,4L)), CustomException.class) // conflict
        );
    }

    // test : Verify that the DTO is correctly composed, if (type ==part) verify call ask partInfo
    @ParameterizedTest
    @MethodSource
    public void getCalendarTest(Calendar calendar) {
        calendar.setCalendarUsers(List.of(1L, 2L));

        // ask : validateYachtUser, getUserInfoList(List<userId>), validatePart(partId)
        // repo : calendar.findById
        Mockito.when(mockCalendarRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(calendar));

        CalendarInfo result = calendarService.getCalendar(0L, userId);

        org.junit.jupiter.api.Assertions.assertAll(
                ()->Assertions.assertThat(result.getPartInfo()).isNotNull(),
                ()->Assertions.assertThat(result.getUserInfoList()).isNotNull(),
                ()->Mockito.verify(mockAskService, Mockito.times(calendar.getPartId() != null ?1:0))
                        .validatePart(Mockito.anyLong())
        );
    }

    public static Stream<Arguments> getCalendarTest() {
        return Stream.of(
                Arguments.of(generateCalendar(0L, CalendarType.PART, yachtId, partId, OffsetDateTime.now(), false)),
                Arguments.of(generateCalendar(0L, CalendarType.SAILING, yachtId, partId, OffsetDateTime.now(), true)),
                Arguments.of(generateCalendar(0L, CalendarType.INSPECTION, yachtId, partId, OffsetDateTime.now(), true))
        );
    }

    // test : Verify that the DTO is correctly composed
    // test 코드 잘못됨, 추후 수정 필요
    @ParameterizedTest
    @MethodSource
    public void getCalendarsTest(
            List<List<Long>> userIdListList,
            List<Long> partIdList,
            List<Long> yachtIdList
    ) {
        // generate CalendarList with userIdList, partIdList
        List<Calendar> calendarList = new ArrayList<>();
        for(int i = 0; i < userIdListList.size(); i++) {
            Calendar calendar = generateCalendar(Integer.valueOf(i).longValue(), CalendarType.PART, yachtIdList.get(i), partIdList.get(i), OffsetDateTime.now(), false);
            calendar.setCalendarUsers(userIdListList.get(i));
            calendarList.add(calendar);
        }

        List yachtInfoList = (List)yachtIdList
                .stream()
                .map(id->Map.of("yacht", ""+id))
                .toList();
        List partInfoList = (List)partIdList
                .stream()
                .map(id->Map.of("part", ""+id))
                .toList();
        List<List<?>> userInfoListList = (List) userIdListList
                .stream()
                .map(idList->
                        idList.stream()
                                .map(id->Map.of("user", ""+id))
                                .toList()
                )
                .toList();

        // repo : calendar.findAllByYachIdList
        Mockito.when(mockCalendarRepository.findAllByYachtOrderByStartDate(Mockito.anyList()))
                .thenReturn(calendarList);
        // ask : getYachtInfoList, getUserInfoListList, getPartInfoList
        Mockito.when(mockAskService.getYachtInfoList(Mockito.anyList()))
                .thenReturn(yachtInfoList);
        Mockito.when(mockAskService.getPartInfoList(Mockito.anyList()))
                .thenReturn(partInfoList);
        Mockito.when(mockAskService.getUserInfoListList(Mockito.anyList()))
                .thenReturn(userInfoListList);
        Mockito.when(mockAskService.yachtListInMemory(Mockito.anyLong()))
                .thenReturn(yachtIdList); // 명백히 잘못된 test 임 --> 추후 askService 수정 이후 다시 작성할 것!

        List<CalendarInfo> resultList = calendarService.getCalendars(0L);

        for(int i = 0; i < resultList.size(); i++) {
            CalendarInfo calendarInfo = resultList.get(i);
            Assertions.assertThat(calendarInfo.getPartInfo())
                    .isEqualTo(partInfoList.get(i));
            Assertions.assertThat(calendarInfo.getYachtInfo())
                    .isEqualTo(yachtInfoList.get(i));
            Assertions.assertThat(calendarInfo.getUserInfoList())
                    .isEqualTo(userInfoListList.get(i));
        }
    }

    public static Stream<Arguments> getCalendarsTest() {
        //            List<List<Long>> userIdListList,
        //            List<Long> partIdList,
        //            List<Long> yachtIdList
        return Stream.of(
                Arguments.of(
                        List.of(List.of(1L, 2L), List.of(1L, 2L), List.of(1L, 2L)),
                        List.of(1L, 1L, 1L),
                        List.of(1L, 1L, 1L)
                ),
                Arguments.of(
                        List.of(List.of(1L, 3L), List.of(2L, 3L), List.of(1L, 2L)),
                        List.of(1L, 2L, 3L),
                        Arrays.asList(1L, null, null)
                )
        );
    }

    // test : set byUserTrue, update(invalid date, competed without review), if(userList.exist) verify ask userInfoList, if completed verify to repair
    @ParameterizedTest
    @MethodSource
    public void updateCalendarTest(CalendarUpdateRequest dto, boolean isError) {
        Calendar calendar = generateCalendar(0L, CalendarType.PART, yachtId, partId, OffsetDateTime.now(), false);
        calendar.setCalendarUsers(List.of(1L, 2L));

        // repo : calendar.findById
        Mockito.when(mockCalendarRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(calendar));

        // ask : validateYachtUser, getUserInfoList
        // web client : add repair

        if(isError) {
            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    ()-> calendarService.updateCalendar(0L, dto, userId)
            );
        }
        else {
            Calendar updateCalendar = calendarService.updateCalendar(0L, dto, userId);

            org.junit.jupiter.api.Assertions.assertAll(
                    ()->Assertions.assertThat(updateCalendar.isByUser()).isTrue(),
                    ()->{
                        boolean changedUserList = dto.getUserList()!=null;
                        Mockito.verify(mockAskService, Mockito.times(changedUserList?1:0))
                            .getUserInfoList(Mockito.anyList());
                        if(changedUserList)
                            Assertions.assertThat(updateCalendar.getCalenderUserIdList())
                                    .isEqualTo(dto.getUserList());
                    },
                    ()-> {
                        boolean makeRepair = dto.getCompleted() && calendar.getType() == CalendarType.PART;
                        Mockito.verify(mockWebClient, Mockito.times(makeRepair?1:0))
                                .webClient(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.notNull());
                    }
            );
        }
    }

    public static Stream<Arguments> updateCalendarTest() {
        OffsetDateTime today = OffsetDateTime.now();
        return Stream.of(
                Arguments.of(generateCalendarUpdateRequest(partId, today, today, false, "content", null, null), false),
                Arguments.of(generateCalendarUpdateRequest(partId, today, today.plusDays(1), false, "content", "review", null), false),
                Arguments.of(generateCalendarUpdateRequest(partId, today, today, false, "content", "review", List.of(1L, 2L, 3L, 4L)), false),
                Arguments.of(generateCalendarUpdateRequest(partId, today, today, true, "content", null, null), true), // set completed without review
                Arguments.of(generateCalendarUpdateRequest(partId, today, today.minusHours(1), false, "content", null, null), true), // invalid date
                Arguments.of(generateCalendarUpdateRequest(partId, today, today, true, "content", "review", List.of(1L, 2L, 3L, 4L)), false)
        );
    }

}

