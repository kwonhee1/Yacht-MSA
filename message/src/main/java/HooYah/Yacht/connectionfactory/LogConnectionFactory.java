package HooYah.Yacht.connectionfactory;

import HooYah.Yacht.Domain;
import HooYah.Yacht.Topic;
import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.publisher.LogStatePublisher;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.SubscribeBehaviour;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class LogConnectionFactory implements ConnectionFactory {

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public MessagePublisher generatePublisher(Topic topic, ObjectMapper objectMapper) {
        return new LogStatePublisher(topic); // just print Log
    }

    @Override
    public void startSubscribe(
            Map<Topic, SubscribeBehaviour<? extends BasedEvent>> subscribeBehaviourMap,
            Domain serverDomain,
            ObjectMapper objectMapper
    ) {
        // cant not Subscribe
    }
}
