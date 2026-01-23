package HooYah.Redis;

import HooYah.Redis.pool.Pool;
import HooYah.RedisData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

public class CacheServiceGenericsTest {

    private Pool pool;

    @BeforeEach
    public void init() {
        pool = Cache.generateInMemoryPool();
    }

    // Tests for getOrSelect
    @Test
    @DisplayName("getOrSelect with Integer")
    public void getOrSelect_Integer() {
        CacheService<Integer> cacheService = new CacheServiceImpl<>("integer", pool, Integer.class);
        CacheService.Select<Integer> select = spy(new CacheService.Select<>() {
            @Override
            public Integer select() {
                return 123;
            }
        });

        Integer value = cacheService.getOrSelect(1L, select);
        assertThat(value).isEqualTo(123);

        value = cacheService.getOrSelect(1L, select);
        Mockito.verify(select, times(1)).select();
    }

    @Test
    @DisplayName("getOrSelect with Long")
    public void getOrSelect_Long() {
        CacheService<Long> cacheService = new CacheServiceImpl<>("long", pool, Long.class);
        CacheService.Select<Long> select = spy(new CacheService.Select<>() {
            @Override
            public Long select() {
                return 456L;
            }
        });

        Long value = cacheService.getOrSelect(1L, select);
        assertThat(value).isEqualTo(456L);

        value = cacheService.getOrSelect(1L, select);
        Mockito.verify(select, times(1)).select();
    }

    @Test
    @DisplayName("getOrSelect with Map (HashMap)")
    public void getOrSelect_Map() {
        CacheService<Map> cacheService = new CacheServiceImpl<>("map", pool, Map.class);
        CacheService.Select<Map> select = spy(new CacheService.Select<>() {
            @Override
            public Map select() {
                Map<String, String> map = new HashMap<>();
                map.put("key", "value");
                return map;
            }
        });

        Map value = cacheService.getOrSelect(1L, select);
        assertThat(value).containsEntry("key", "value");

        value = cacheService.getOrSelect(1L, select);
        Mockito.verify(select, times(1)).select();
    }

    @Test
    @DisplayName("getOrSelect with Custom Class (RedisData)")
    public void getOrSelect_CustomClass() {
        CacheService<RedisData> cacheService = new CacheServiceImpl<>("custom", pool, RedisData.class);
        CacheService.Select<RedisData> select = spy(new CacheService.Select<>() {
            @Override
            public RedisData select() {
                return new RedisData(1);
            }
        });

        RedisData value = cacheService.getOrSelect(1L, select);
        assertThat(value.getData()).isEqualTo(1);

        value = cacheService.getOrSelect(1L, select);
        Mockito.verify(select, times(1)).select();
    }

    @Test
    @DisplayName("getOrSelect with List (ArrayList)")
    public void getOrSelect_List() {
        CacheService<List> cacheService = new CacheServiceImpl<>("list", pool, List.class);
        CacheService.Select<List> select = spy(new CacheService.Select<>() {
            @Override
            public List select() {
                List<String> list = new ArrayList<>();
                list.add("item1");
                return list;
            }
        });

        List value = cacheService.getOrSelect(1L, select);
        assertThat(value).contains("item1");

        value = cacheService.getOrSelect(1L, select);
        Mockito.verify(select, times(1)).select();
    }

    @Test
    public void getOrSelect_LongList() {
        CacheService<List> cacheService = new CacheServiceImpl<>("longlist", pool, List.class);
        CacheService.Select<List> select = spy(new CacheService.Select<>() {
            @Override
            public List select() {
                return List.of(1L, 2L, 3L, 4L);
            }
        });

        List<Long> value = cacheService.getOrSelect(1L, select);
        assertThat(value).containsAll(List.of(1L, 2L, 3L, 4L));
        assertThat(value.getFirst()).isInstanceOf(Long.class);

        value = cacheService.getOrSelect(1L, select);
        assertThat(value).containsAll(List.of(1L, 2L, 3L, 4L));
        assertThat(value.getFirst()).isInstanceOf(Long.class); // 이후 cache에서 꺼내는 값이 Integer가 됨 (casting이 되지 않음!) 시발
        Mockito.verify(select, times(1)).select();
    }

    // Tests for getListOrSelect
    @Test
    @DisplayName("getListOrSelect with List<Integer>")
    public void getListOrSelect_IntegerList() {
        CacheService<Integer> cacheService = new CacheServiceImpl<>("integerList", pool, Integer.class);
        CacheService.Select<List<Integer>> select = spy(new CacheService.Select<>() {
            @Override
            public List<Integer> select() {
                return List.of(1, 2, 3);
            }
        });

        List<Integer> value = cacheService.getListOrSelect(List.of(1L, 2L, 3L), select);
        assertThat(value).containsExactly(1, 2, 3);

        value = cacheService.getListOrSelect(List.of(1L, 2L, 3L), select);
        Mockito.verify(select, times(1)).select();
    }

    @Test
    @DisplayName("getListOrSelect with List<CustomClass>")
    public void getListOrSelect_CustomClassList() {
        CacheService<RedisData> cacheService = new CacheServiceImpl<>("customList", pool, RedisData.class);
        CacheService.Select<List<RedisData>> select = spy(new CacheService.Select<>() {
            @Override
            public List<RedisData> select() {
                return List.of(new RedisData(1), new RedisData(2));
            }
        });

        List<RedisData> value = cacheService.getListOrSelect(List.of(1L, 2L), select);
        assertThat(value).hasSize(2);
        assertThat(value.get(0).getData()).isEqualTo(1);
        assertThat(value.get(1).getData()).isEqualTo(2);

        value = cacheService.getListOrSelect(List.of(1L, 2L), select);
        Mockito.verify(select, times(1)).select();
    }

    @Test
    @DisplayName("getListOrSelect with List<Map>")
    public void getListOrSelect_MapList() {
        CacheService<Map> cacheService = new CacheServiceImpl<>("mapList", pool, Map.class);
        CacheService.Select<List<Map>> select = spy(new CacheService.Select<>() {
            @Override
            public List<Map> select() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("key1", "value1");
                Map<String, String> map2 = new HashMap<>();
                map2.put("key2", "value2");
                return List.of(map1, map2);
            }
        });

        List<Map> value = cacheService.getListOrSelect(List.of(1L, 2L), select);
        assertThat(value).hasSize(2);
        assertThat(value.get(0)).containsEntry("key1", "value1");
        assertThat(value.get(1)).containsEntry("key2", "value2");

        value = cacheService.getListOrSelect(List.of(1L, 2L), select);
        Mockito.verify(select, times(1)).select();
    }
}
