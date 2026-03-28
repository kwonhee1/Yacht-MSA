package HooYah.Yacht.conf;

import HooYah.Yacht.Domain;
import HooYah.Yacht.MessageQue;
import HooYah.Yacht.Topic;
import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.SubscribeBehaviour;
import HooYah.Yacht.yacht.event.YachtCreateEvent;
import HooYah.Yacht.yacht.service.YachtService;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class MessageQueConfig {

    private final YachtService yachtService;
    private MessageQue messageQue;

    public MessageQueConfig(@Lazy YachtService yachtService) {
        this.yachtService = yachtService;
    }

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
        messageQue = new MessageQue(Domain.YACHT, ConnectionFactory.redisConnectionFactory(host, port, username, password));

        Map<Topic, SubscribeBehaviour<? extends BasedEvent>> subscribeBehaviour = SubscribeBehaviour.builder()
                .add(Topic.USER_DELETE, DeletedEvent.class, (DeletedEvent event) -> yachtService.deleteByUser(event.getUserIdValue()))
                .build();

        messageQue.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<DeletedEvent> yachtDeleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.YACHT_DELETE);
    }

    @Bean
    public MessagePublisher<YachtCreateEvent> yachtCreateMessagePublisher() {
        return messageQue.generatePublisher(Topic.YACHT_CREATE);
    }

}
