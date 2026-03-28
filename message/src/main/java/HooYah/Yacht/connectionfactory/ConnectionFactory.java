package HooYah.Yacht.connectionfactory;

import HooYah.Yacht.Domain;
import HooYah.Yacht.Topic;
import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.SubscribeBehaviour;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public interface ConnectionFactory {

    boolean isRunning();

    <T extends BasedEvent> MessagePublisher<T> generatePublisher(
            Topic topic,
            ObjectMapper objectMapper // todo : 책임 분리 필요
    );
    void startSubscribe(
            Map<Topic, SubscribeBehaviour<? extends BasedEvent>> subscribeBehaviourMap,
            Domain serverDomain,
            ObjectMapper objectMapper // todo : 책임 분리 필요
    );

    static ConnectionFactory logConnectionFactory() {
        return new LogConnectionFactory();
    }

    static ConnectionFactory redisConnectionFactory(
            String host,
            int port,
            String user,
            String password
    ) {
        return new RedisConnectionFactory(host, port, user, password);
    }

}
