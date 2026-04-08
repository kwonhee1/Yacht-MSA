package HooYah.Yacht.service;

import static HooYah.Yacht.TestUtil.*;

import HooYah.Redis.Cache;
import HooYah.Redis.CacheService;
import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import HooYah.Yacht.repository.PartRepository;
import HooYah.Yacht.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.support.TransactionTemplate;

@Disabled
public class RepairServiceTest {

    private RepairService repairService;

    private PartRepository mockPartRepository;
    private RepairRepository mockRepairRepository;

    private CacheService inMemoryCache;
    private WebClient mockWebClient;

    @BeforeEach
    public void init() {
        mockPartRepository = Mockito.mock(PartRepository.class);
        mockRepairRepository = Mockito.mock(RepairRepository.class);

        inMemoryCache = Cache.cacheService("category", Cache.generateInMemoryPool()); // not mocked
        mockWebClient = Mockito.spy(WebClient.class);

        TransactionTemplate transactionTemplate = Mockito.mock(TransactionTemplate.class);
        Mockito.lenient().when(transactionTemplate.execute(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        repairService = new RepairService(mockRepairRepository, mockPartRepository, Mockito.mock(UpdateCalendarAndAlarmService.class), inMemoryCache, mockWebClient, transactionTemplate);
    }

    // test :
    @Test
    public void getRepairListByPartTest() {
        // skip
    }

    // test : check call updateCalendarAndAlarm api
    @Test
    public void addRepairTest() {
        Long yachtId = 1L;
        Long userId = 0L;

        Part part = generatePart(0L, yachtId);

        // repo : part.findById, repair.findByIdOrderByRepairDateDesc
        Mockito.when(mockPartRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(part));
        Mockito.when(mockRepairRepository.findByIdOrderByRepairDateDesc(Mockito.anyLong()))
                .thenReturn(Optional.of(generateRepair(0L, part, userId))); // 아무 repair를 반환
        // web client : validateYachtUser(get), updateCalendarAndAlarm(post)
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.isNull()).toMap())
                .thenReturn(Map.of("yacht", "yacht"));
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.isNotNull()).toMap())
                .thenReturn(Map.of("message", "success"));

        repairService.addRepair(part.getId(), "content", OffsetDateTime.now(), userId);

        Mockito.verify(mockWebClient, Mockito.times(1)).webClient(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.isNull()).toMap();
        Mockito.verify(mockWebClient, Mockito.times(1)).webClient(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.isNotNull()).toMap();
    }

    // test : check update, check call updateCalendarAndAlarm api
    @Test
    public void updateRepairTest(){
        Long yachtId = 1L;
        Long userId = 0L;

        Part part = generatePart(0L, yachtId);
        Repair repair = generateRepair(0L, part, userId);

        // repo : repair.findById, repair.findByIdOrderByRepairDateDesc
        Mockito.when(mockRepairRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(repair));
        Mockito.when(mockRepairRepository.findByIdOrderByRepairDateDesc(Mockito.anyLong()))
                .thenReturn(Optional.of(repair));
        // web client : validateYachtUser(get), updateCalendarAndAlarm(post)
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.isNull()).toMap())
                .thenReturn(Map.of("yacht", "yacht"));
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.isNotNull()).toMap())
                .thenReturn(Map.of("message", "success"));

        Repair updatedRepair = repairService.updateRepair(part.getId(), "content", null, userId);
        Assertions.assertThat(updatedRepair.getContent()).isEqualTo("content");
        Mockito.verify(mockWebClient, Mockito.times(0))
                        .webClient(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.isNotNull()).toMap();

        updatedRepair = repairService.updateRepair(part.getId(), "content2", OffsetDateTime.now(), userId);
        Assertions.assertThat(updatedRepair.getContent()).isEqualTo("content2");
        Mockito.verify(mockWebClient, Mockito.times(1))
                .webClient(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.isNotNull()).toMap();
    }

    // test : delete, send message que delete
    @Test
    public void deleteRepairTest(){
        // skip
    }

}
