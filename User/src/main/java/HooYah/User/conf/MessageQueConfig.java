package HooYah.User.conf;

import HooYah.Yacht.Domain;
import HooYah.Yacht.MessageQue;
import HooYah.Yacht.Topic;
import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.CreateEvent;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.Behaviour;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueConfig {

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
        messageQue = new MessageQue(Domain.USER, ConnectionFactory.redisConnectionFactory(host, port, username, password));

        Map subscribeBehaviour = Behaviour.builder()
                // nothing to subscribe
                .build();

        messageQue.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<DeletedEvent> userDeleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.USER_DELETE);
    }

    @Bean
    public MessagePublisher<CreateEvent> userCreateMessagePublisher() {
        return messageQue.generatePublisher(Topic.USER_CREATE);
    }

}
