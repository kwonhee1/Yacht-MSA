package HooYah.Yacht.service;

import static HooYah.Yacht.TestUtil.*;

import HooYah.Redis.RedisService;
import HooYah.Redis.RedisServiceImpl;
import HooYah.Redis.pool.ConnectionPool;
import HooYah.Yacht.part.domain.Part;
import HooYah.Yacht.part.dto.request.UpdatePartDto;
import HooYah.Yacht.part.dto.response.PartDto;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.part.service.PartService;
import HooYah.Yacht.repair.domain.Repair;
import HooYah.Yacht.repair.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

// @SpringBootTest
public class PartServiceTest {

    private PartService partService;

    private PartRepository mockPartRepository;
    private RepairRepository mockRepairRepository;

    private RedisService inMemoryRedis;
    private WebClient mockWebClient;

    @BeforeEach
    public void init() {
        mockPartRepository = Mockito.mock(PartRepository.class);
        mockRepairRepository = Mockito.mock(RepairRepository.class);

        inMemoryRedis = new RedisServiceImpl("category", ConnectionPool.generate("", 0, "", "", 3)); // not mocked
        mockWebClient = Mockito.mock(WebClient.class);

        partService = new PartService(mockPartRepository, mockRepairRepository, inMemoryRedis, mockWebClient);
    }

    @Test
    public void getPartListByYachtTest() {
        Long userId = 1L;
        Long yachtId = 1L;

        List<Part> partList = List.of(
                generatePart(0L, yachtId), null
        );
        List<Repair> repairList = List.of(
                generateRepair(0L, partList.get(0), userId), generateRepair(1L, partList.get(1), userId)
        );

        // repo : part.findPartListByYacht(yachtId), repair.findAllLastRepair(List<PartId>)
        Mockito.when(mockPartRepository.findPartListByYacht(Mockito.anyLong())).thenReturn(partList);
        Mockito.when(mockRepairRepository.findAllLastRepair(Mockito.any(List.class))).thenReturn(repairList);
        // web client : validateYachtUser (return not null)
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any()))
                .thenReturn(Map.of("yacht", "yacht"));

        List<PartDto> result = partService.getPartListByYacht(yachtId, userId);

        org.junit.jupiter.api.Assertions.assertAll(
                ()->Assertions.assertThat(result.size()).isEqualTo(2),
                ()->Assertions.assertThat(result.get(0).getLastRepair())
                        .isEqualTo(repairList.get(0).getRepairDate()),
                ()->Assertions.assertThat(result.get(1).getLastRepair())
                        .isNull()
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
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.nullable(Object.class))) // ask yacht
                .thenReturn(Map.of("yacht", "yacht")); // return not null
        Mockito.when(mockWebClient.webClient(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(Object.class))) // send to calendar
                .thenReturn(Map.of("message", "success")); // return null

        partService.updatePart(updateDto, userId);

        org.junit.jupiter.api.Assertions.assertAll(
                ()->Assertions.assertThat(part.getName()).isEqualTo(updateDto.getName()),
                ()->Mockito.verify(mockWebClient, Mockito.times(updateDto.getInterval()!=null?1:0))
                        .webClient(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.isNotNull())
        );
    }

    // test : send message que (일단 skip)
    @Test
    public void deletePartTest() {

    }

}
