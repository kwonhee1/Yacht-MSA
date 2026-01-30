package HooYah.Yacht.bean;

import HooYah.Yacht.Domain;
import HooYah.Yacht.MessageQue;
import HooYah.Yacht.Topic;
import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.event.LastRepairChangedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.Behaviour;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Configuration
@Disabled // redis Server need!
public class BeanGenerateTest {

    @Value("${mq.host}")
    private String host;
    @Value("${mq.port}")
    private int port;
    @Value("${mq.username}")
    private String username;
    @Value("${mq.password}")
    private String password;

    private MessageQue messageQue;

    @BeforeEach
    public void beforeEach() {
        messageQue = new MessageQue(Domain.PART, ConnectionFactory.redisConnectionFactory(host, port, username, password));

        Map subscribeBehaviour = Behaviour.builder()
                .add(Topic.YACHT_DELETE, (DeletedEvent event)->System.out.println("delete parts by yacht id"))
                .build();

        messageQue.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<DeletedEvent> partDeletePublisher() {
        return messageQue.generatePublisher(Topic.PART_DELETE);
    }

    @Bean
    public MessagePublisher<LastRepairChangedEvent> repairChangedPublisher() {
        return messageQue.generatePublisher(Topic.PART_INTERVAL_CHANGED);
    }

    @Test
    public void main() {
        partDeletePublisher().publish(new DeletedEvent(1L, 1L));
    }

}
