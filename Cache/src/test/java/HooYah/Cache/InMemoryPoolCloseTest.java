package HooYah.Cache;

import HooYah.Cache.CacheService.Select;
import HooYah.Cache.connection.SaveSecond;
import HooYah.Cache.pool.InMemoryPool;
import HooYah.Cache.connection.Connection;
import HooYah.Cache.pool.Pool;
import HooYah.Cache.template.CacheTemplate;
import HooYah.RedisData;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryPoolCloseTest {

    @Test
    public void testCloseThrowsIllegalMonitorStateException() {
        Pool pool = Cache.generateInMemoryPool();
        Connection conn = pool.getConnection();

        Assertions.assertDoesNotThrow(()->pool.close());
    }

    @Test
    @DisplayName("pool close 확인")
    public void poolCloseTest() throws InterruptedException {
        AtomicReference<Pool> pool = new AtomicReference<>(Cache.generateInMemoryPool());

        CountDownLatch latch = new CountDownLatch(3);
        for(int i=0;i<3;i++) {
            new Thread(() -> {
                Connection con = pool.get().getConnection();
                try {
                    Thread.sleep(10000L);
                    latch.countDown();

                    // already closed by main thread!
                    Assertions.assertThrows(ResourceClosedException.class, ()->con.get("", new SaveSecond(5)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(1000L); // 1초 기다림 --> Thread 들이 Connection을 얻을 시간

        WeakReference<Pool> weakReference = new WeakReference<>(pool.get());
        pool.get().close();
        pool.set(null);

        for (int i = 0; i < 50; i++) {
            System.gc();
            if (weakReference.get() == null) {
                break;
            }
            Thread.sleep(100);
        }

        latch.await();

        Assertions.assertNull(weakReference.get());
    }
}
