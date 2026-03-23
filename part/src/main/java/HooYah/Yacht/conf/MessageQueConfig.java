package HooYah.Yacht.conf;

import HooYah.Yacht.Domain;
import HooYah.Yacht.MessageQue;
import HooYah.Yacht.Topic;
import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.event.LastRepairChangedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.service.PartService;
import HooYah.Yacht.subscriber.Behaviour;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MessageQueConfig {

    private final PartService partService;
    private MessageQue messageQue;

    @Value("${mq.host}")
    private String host;
    @Value("${mq.port}")
    private int port;
    @Value("${mq.username}")
    private String username;
    @Value("${mq.password}")
    private String password;

    @PostConstruct
    public void init() {
        messageQue = new MessageQue(Domain.PART, ConnectionFactory.redisConnectionFactory(host, port, username, password));

        Map subscribeBehaviour = Behaviour.builder()
                .add(Topic.YACHT_DELETE, (DeletedEvent event) -> partService.deleteYacht(event))
                .build();

        messageQue.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<DeletedEvent> partDeleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.PART_DELETE);
    }

    @Bean
    public MessagePublisher<LastRepairChangedEvent> partIntervalChangedMessagePublisher() {
        return messageQue.generatePublisher(Topic.PART_INTERVAL_CHANGED);
    }

    @Bean
    public MessagePublisher<LastRepairChangedEvent> repairAddMessagePublisher() {
        return messageQue.generatePublisher(Topic.REPAIR_ADD);
    }

}
