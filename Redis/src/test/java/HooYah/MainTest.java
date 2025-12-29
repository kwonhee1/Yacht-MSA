package HooYah;

import HooYah.Redis.pool.JedisPool;
import HooYah.Redis.RedisService;
import HooYah.Redis.RedisService.Select;
import HooYah.Redis.RedisServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.GetExParams;

public class MainTest {

    Jedis mockedJedis = Mockito.mock(Jedis.class);
    JedisPool mockedJedisPool = Mockito.mock(JedisPool.class);

    @BeforeEach
    public void afterInit() {
        Mockito.lenient().when(mockedJedisPool.getConnection()).thenReturn(mockedJedis);

        // set (String key, String value, SetParams setParam)
        //Mockito.lenient().when(mockedJedis.set(Mockito.anyString(), Mockito.anyString(), Mockito.any(SetParams.class)))
        //        .thenReturn();

//        // getEx(String key, GetExParam getParam
//        Mockito.lenient().when(mockedJedis.getEx(Mockito.anyString(), Mockito.any(GetExParams.class)))
//                .thenReturn("조회 내용");
    }

    @Test
    @DisplayName("select 값이 없을 때 select가 실행되는지 확인")
    public void getOrSelectTest() {
        RedisService redisService = new RedisConfig().userRedisService();

        Select select = Mockito.mock(Select.class);
        Mockito.when(select.select()).thenReturn(Optional.of(new Data(1L, "test")));

        redisService.getOrSelect(1L, select);

        Mockito.verify(select, Mockito.times(1)).select();
    }

    @Test
    @DisplayName("값이 존재 하면 select가 실행되지 않는지 검증")
    public void setAndGetTest() {
        RedisService redisService = new RedisConfig().userRedisService();
        Data data = new Data(1L, "test");
        Mockito.when(mockedJedis.getEx(Mockito.anyString(), Mockito.any(GetExParams.class)))
                .thenReturn(data);

        Select select = Mockito.mock(Select.class);
        Mockito.when(select.select()).thenReturn(Optional.of(new Data(1L, "test")));

        redisService.add();
    }

    class Data {
        private Long id;
        private String name;

        public Data(Long id, String name) { this.id = id; this.name = name; }
    }

    class RedisConfig {
        public RedisService userRedisService() {
            return new RedisServiceImpl("REDIS_USER_MODULE_NAME", mockedJedisPool);
        }
    }

}
