package HooYah.Cache;

import HooYah.Cache.connection.SaveSecond;
import HooYah.Cache.pool.ConnectFailException;
import HooYah.Cache.pool.JedisPool;
import HooYah.Cache.pool.Pool;
import HooYah.Cache.template.CacheTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CacheTemplateReconnectTest {

    @Test
    void reconnectUsesOriginalPoolAfterFallback() {
        Pool redisPool = Mockito.mock(JedisPool.class);
        Pool newRedisPool = Mockito.mock(JedisPool.class);

        Mockito.when(redisPool.getConnection())
                .thenThrow(new ConnectFailException(new RuntimeException("redis down")));
        Mockito.when(redisPool.copyNew()).thenReturn(newRedisPool);

        CacheTemplate cacheTemplate = new CacheTemplate(redisPool);

        cacheTemplate.add("key", "value", new SaveSecond(10L)); // switch to inmemory
        Mockito.verify(redisPool).getConnection();

        cacheTemplate.reconnect();
        Mockito.verify(redisPool).copyNew();
        Assertions.assertThrows(NullPointerException.class,
                ()-> cacheTemplate.add("key", "value", new SaveSecond(10L))
        );
    }
    
}
