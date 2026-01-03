package HooYah;

import HooYah.Redis.pool.ConnectionPool;
import HooYah.Redis.RedisService;
import HooYah.Redis.RedisService.Select;
import HooYah.Redis.RedisServiceImpl;
import HooYah.Redis.pool.Pool;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MainTest {

    private Pool pool;
    private RedisService testRedisService;

    private Select selectOne;

    @BeforeEach
    public void init() {
        pool = ConnectionPool.generate("", 1, "", "", 3);
        testRedisService = new RedisServiceImpl("test", pool);

        selectOne = Mockito.spy(new Select<Optional>() {
            @Override
            public Optional select() {
                System.out.println("selected");
                return Optional.of(new RedisData(1));
            }
        });
    }

    @Test
    @DisplayName("select 값이 없을 때 select가 실행되는지 확인")
    public void getOrSelectTest() {
        testRedisService.getOrSelect(1L, selectOne);

        Mockito.verify(selectOne, Mockito.times(1)).select();
    }

    @Test
    @DisplayName("값이 존재 하면 select가 실행되지 않는지 검증")
    public void setAndGetTest() {
        testRedisService.getOrSelect(1L, selectOne);
        testRedisService.getOrSelect(1L, selectOne);

        Mockito.verify(selectOne, Mockito.times(1)).select();
    }

}
