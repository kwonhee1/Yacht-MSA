package HooYah.User.conf;

import HooYah.User.user.event.UserCreateEvent;
import HooYah.User.user.service.UserService;
import HooYah.Yacht.Domain;
import HooYah.Yacht.MessageQue;
import HooYah.Yacht.Topic;
import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.SubscribeBehaviour;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class MessageQueConfig {

    private final UserService userService;
    private MessageQue messageQue;

    public MessageQueConfig(@Lazy UserService userService) {
        this.userService = userService;
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
        messageQue = new MessageQue(Domain.USER, ConnectionFactory.redisConnectionFactory(host, port, username, password));

        Map<Topic, SubscribeBehaviour<? extends BasedEvent>> subscribeBehaviour = SubscribeBehaviour.builder()
                // nothing to subscribe
                .build();

        messageQue.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<DeletedEvent> userDeleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.USER_DELETE);
    }

    @Bean
    public MessagePublisher<UserCreateEvent> userCreateMessagePublisher() {
        return messageQue.generatePublisher(Topic.USER_CREATE);
    }

}
