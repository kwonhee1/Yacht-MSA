package HooYah.Yacht.service;

import HooYah.Redis.Cache;
import HooYah.Redis.CacheService;
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
        CacheService inMemoryCacheService = Cache.cacheService("", Cache.generateInMemoryPool());
        dataMap.forEach((k,v)->{
            inMemoryCacheService.add(k,v);
        });

        AskService askService = Mockito.mock(AskService.class);
        AskService.Shared shared = askService.new Shared();

        List<List<?>> resultList = shared.getListList(idList, inMemoryCacheService, (distinctIdList)->{throw new RuntimeException();});

        for(int y = 0; y < idList.size(); y++){
            for(int x = 0; x < idList.get(y).size(); x++){
                Map<String, Object> resultMap = (Map<String, Object>) resultList.get(y).get(x);
                Data expectedData = dataMap.get(idList.get(y).get(x));
                Assertions.assertThat(((Number) resultMap.get("id")).longValue()).isEqualTo(expectedData.getId());
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
