package HooYah.Yacht.conf;

import HooYah.Yacht.Domain;
import HooYah.Yacht.MessageQue;
import HooYah.Yacht.Topic;
import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.Behaviour;
import HooYah.Yacht.yacht.service.YachtUserService;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MessageQueConfig {

    private final YachtUserService yachtUserService;
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
        messageQue = new MessageQue(Domain.YACHT, ConnectionFactory.redisConnectionFactory(host, port, username, password));

        Map subscribeBehaviour = Behaviour.builder()
                .add(Topic.USER_DELETE, (DeletedEvent event) -> yachtUserService.deleteUser(event))
                .build();

        messageQue.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<DeletedEvent> yachtDeleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.YACHT_DELETE);
    }

}
