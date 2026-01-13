package HooYah.Yacht.service;

import HooYah.Redis.RedisService;
import HooYah.Redis.RedisServiceImpl;
import HooYah.Redis.pool.ConnectionPool;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

public class AskServiceTest {

    @ParameterizedTest
    @MethodSource
    public void sharedGetListListTest(
            Map<Long, Data> dataMap,
            List<List<Long>> idList
    ) {
        RedisService inMemoryRedisService = new RedisServiceImpl("", ConnectionPool.generate("", 0, "", "", 3));
        dataMap.forEach((k,v)->{
            inMemoryRedisService.add(k,v);
        });

        AskService askService = Mockito.mock(AskService.class);
        AskService.Shared shared = askService.new Shared();

        List<List<?>> resultList = shared.getListList(idList, inMemoryRedisService, (distinctIdList)->{throw new RuntimeException();});

        for(int y = 0; y < idList.size(); y++){
            for(int x = 0; x < idList.get(y).size(); x++){
                Assertions.assertThat(resultList.get(y).get(x))
                        .isEqualTo(dataMap.get(idList.get(y).get(x)));
            }
        }
    }

    public static Stream<Arguments> sharedGetListListTest() {
        return Stream.of(
                Arguments.of(
                        Map.of(1L, new Data(1L), 2L, new Data(2L), 3L, new Data(3L)),
                        List.of(List.of(1L, 2L), List.of(2L, 3L, 1L))
                )
        );
    }

    public static class Data {
        Long id;
        public Data(Long id) {
            this.id = id;
        }
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
    }
}
