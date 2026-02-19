package HooYah.Gateway.provider;

import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.vo.Host;
import HooYah.Gateway.loadbalancer.domain.vo.Protocol;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LimiterTest {
    @Test
    @DisplayName("여러 스레드에서 동시에 UpCount를 호출해도 MaxCount를 넘지 않아야 한다")
    void concurrencyTest() throws InterruptedException {

        Pod targetPod = Pod.running("pod", new Server("server", Protocol.http, new Host("serverhost"), 3), null);

        long maxLimit = 10;
        Limiter limiter = new Limiter(Map.of(targetPod, maxLimit));

        int threadCount = 100; // 100개의 스레드가 동시에 시도
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicLong concurrentCount = new AtomicLong(0);
        AtomicLong maxCount = new AtomicLong(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    while(!limiter.tryUpCount(targetPod)) {
                        Thread.sleep(100); // 성공 할 떄 까지 계속 시도 0.1
                    }

                    synchronized (concurrentCount) {
                        Long concurrent = concurrentCount.incrementAndGet();
                        if(concurrent > maxCount.get())
                            maxCount.set(concurrent);
                    }
                    Thread.sleep(500); // 작업 0.5초

                    concurrentCount.decrementAndGet();
                    limiter.downCount(targetPod);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    Assertions.fail(e);
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        System.out.println(maxCount +", " + concurrentCount.get());

        Assertions.assertTrue(maxCount.get() <= maxLimit);
    }
}