package HooYah.Yacht.service;

import static HooYah.Yacht.TestUtil.*;

import HooYah.Redis.Cache;
import HooYah.Redis.CacheService;
import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import HooYah.Yacht.dto.part.PartDto;
import HooYah.Yacht.dto.part.UpdatePartDto;
import HooYah.Yacht.repository.PartRepository;
import HooYah.Yacht.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.transaction.support.TransactionTemplate;

// @SpringBootTest
@Disabled
public class PartServiceTest {

    private PartService partService;

    private PartRepository mockPartRepository;
    private RepairRepository mockRepairRepository;

    private CacheService inMemoryCache;
    private WebClient mockWebClient;

    private RepairService mockedRepairService;

    @BeforeEach
    public void init() {
        mockPartRepository = Mockito.mock(PartRepository.class);
        mockRepairRepository = Mockito.mock(RepairRepository.class);

        inMemoryCache = Cache.cacheService("category", Cache.generateInMemoryPool()); // not mocked
        mockWebClient = Mockito.mock(WebClient.class);

        mockedRepairService = Mockito.mock(RepairService.class);

        TransactionTemplate transactionTemplate = Mockito.mock(TransactionTemplate.class);
        Mockito.lenient().when(transactionTemplate.execute(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));


        partService = new PartService(mockPartRepository, mockRepairRepository, Mockito.mock(UpdateCalendarAndAlarmService.class), mockedRepairService, transactionTemplate, inMemoryCache, mockWebClient);
    }

    @Test
    public void getPartListByYachtTest() {
        Long userId = 1L;
        Long yachtId = 1L;

        List<Part> partList = List.of(
                generatePart(0L, yachtId), generatePart(1L, yachtId)
        );
        List<Repair> repairList = List.of(
                generateRepair(0L, partList.get(0), userId), generateRepair(1L, partList.get(1), userId)
        );

        // repo : part.findPartListByYacht(yachtId), repair.findAllLastRepair(List<PartId>)
        Mockito.when(mockPartRepository.findPartListByYacht(Mockito.anyLong())).thenReturn(partList);
        Mockito.when(mockRepairRepository.findAllLastRepair(Mockito.any(List.class))).thenReturn(repairList);
        // web client : validateYachtUser (return not null)
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any()).toMap())
                .thenReturn(Map.of("yacht", "yacht"));

        List<PartDto> result = partService.getPartListByYacht(yachtId, userId);

        org.junit.jupiter.api.Assertions.assertAll(
                ()->Assertions.assertThat(result.size()).isEqualTo(2),
                ()->Assertions.assertThat(result.get(0).getLastRepair())
                        .isEqualTo(repairList.get(0).getRepairDate()),
                ()->Assertions.assertThat(result.get(1).getLastRepair())
                        .isNotNull()
        );
    }

    public void addPartTest() {
        // skip
    }

    public static Stream<Arguments> updatePartTest() {
        return Stream.of(
                Arguments.of(generateUpdatePartDto("updated name", null)),
                Arguments.of(generateUpdatePartDto("updated name", 1L))
        );
    }

    // check : update, call auto update calendar api
    @ParameterizedTest
    @MethodSource
    public void updatePartTest(UpdatePartDto updateDto) {
        Long userId = 1L;
        Long yachtId = 1L;

        Part part = generatePart(0L, yachtId);
        updateDto.setId(part.getId());

        // repo : part.findById, repair.findByIdOrderByRepairDateDesc(partId)
        Mockito.when(mockPartRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(part));
        Mockito.when(mockRepairRepository.findByIdOrderByRepairDateDesc(Mockito.anyLong()))
                .thenReturn(Optional.of(generateRepair(0L, part, userId)));
        // web : validateYachtUser(get), updateCalendarAndAlarm(post)
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.nullable(Object.class)).toMap()) // ask yacht
                .thenReturn(Map.of("yacht", "yacht")); // return not null
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(Object.class)).toMap()) // send to calendar
                .thenReturn(Map.of("message", "success")); // return null

        partService.updatePart(updateDto, userId);

        org.junit.jupiter.api.Assertions.assertAll(
                ()->Assertions.assertThat(part.getName()).isEqualTo(updateDto.getName()),
                ()->Mockito.verify(mockWebClient, Mockito.times(updateDto.getInterval()!=null?1:0))
                        .webClient(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.isNotNull()).toMap()
        );
    }

    // test : send message que (일단 skip)
    @Test
    public void deletePartTest() {

    }

}
