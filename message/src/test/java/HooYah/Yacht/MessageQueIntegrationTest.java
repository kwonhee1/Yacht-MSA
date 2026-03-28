package HooYah.Yacht;

import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.SubscribeBehaviour;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

@Configuration
@SpringBootTest
@Disabled // redis Server need
public class MessageQueIntegrationTest {

    private MessageQue setting;

    @Value("${mq.host}")
    private String host;
    @Value("${mq.port}")
    private int port;
    @Value("${mq.username}")
    private String username;
    @Value("${mq.password}")
    private String password;

    @BeforeEach
    void setUp() {
        setting = new MessageQue(Domain.YACHT, ConnectionFactory.redisConnectionFactory(host, port, username, password));
    }

    @Test
    @DisplayName("실제 Redis 서버 연결 및 발행/구독 통합 테스트")
    void testRealRedisPublishAndSubscribe() throws InterruptedException {
        // given
        CountDownLatch latch = new CountDownLatch(1);

        Map behaviors = SubscribeBehaviour
                .builder()
                .add(Topic.YACHT_DELETE, DeletedEvent.class, (event)->{ System.out.println("sbuscribe!"); latch.countDown();})
                .build();

        // when
        setting.startSubscribe(behaviors);

        Thread.sleep(2000);

        MessagePublisher<BasedEvent> publisher = setting.generatePublisher(Topic.YACHT_DELETE);
        BasedEvent basedEvent = new DeletedEvent(1L, 1L);
        publisher.publish(basedEvent);

        // 비동기 처리를 기다림 (최대 20초)
        boolean waitSuccess = latch.await(20, TimeUnit.SECONDS);

        // then
        assertTrue(waitSuccess);
    }
}
