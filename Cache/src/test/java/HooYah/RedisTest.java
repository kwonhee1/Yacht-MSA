package HooYah;

import HooYah.Cache.Cache;
import HooYah.Cache.CacheService;
import HooYah.Cache.pool.Pool;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RedisTest {

    private final Pool redisPool = Cache.generateRedisPool("yacht.r-e.kr", 6379, "default", null, 3);
    private CacheService<RedisData> cacheService = Cache.cacheService("test", redisPool, RedisData.class);

    @Test
    public void test() {
        cacheService.add(1L, new RedisData(1));
        cacheService.add(2L, new RedisData(2));
        cacheService.add(3L, new RedisData(3));
        cacheService.add(4L, new RedisData(4));

        cacheService.getOrSelect(1L, ()->{throw new RuntimeException("already exist");});
        cacheService.getListOrSelect(List.of(1L, 2L, 3L), ()->{throw new RuntimeException("already exist");});

        cacheService.getListOrSelect(List.of(1L, 6L, 5L), ()->{throw new RuntimeException("ok");});
    }

}
