package HooYah;

import HooYah.Cache.Cache;
import HooYah.Cache.CacheService;
import HooYah.Cache.CacheService.Select;
import HooYah.Cache.CacheServiceImpl;
import HooYah.Cache.pool.Pool;
import HooYah.Cache.template.CacheTemplate;
import java.lang.ref.WeakReference;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MainTest {

    private Pool pool;
    private CacheService testCacheService;

    private Select selectOne;

    @BeforeEach
    public void init() {
        pool = Cache.generateInMemoryPool();
        testCacheService = Cache.cacheService("test", pool, Map.class);
        testCacheService = new CacheServiceImpl("test", new CacheTemplate(pool), Map.class);

        selectOne = Mockito.spy(new Select<Object>() {
            @Override
            public Object select() {
                return new RedisData(1);
            }
        });
    }

    @Test
    @DisplayName("select 값이 없을 때 select가 실행되는지 확인")
    public void getOrSelectTest() {
        testCacheService.getOrSelect(1L, selectOne);

        Mockito.verify(selectOne, Mockito.times(1)).select();
    }

    @Test
    @DisplayName("값이 존재 하면 select가 실행되지 않는지 검증")
    public void setAndGetTest() {
        testCacheService.getOrSelect(1L, selectOne);
        testCacheService.getOrSelect(1L, selectOne);

        Mockito.verify(selectOne, Mockito.times(1)).select();
    }

}
