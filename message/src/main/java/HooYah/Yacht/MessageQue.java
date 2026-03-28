package HooYah.Yacht;

import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.SubscribeBehaviour;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Map;

public class MessageQue {

    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;
    private final Domain serverDomain;

    public MessageQue(
            Domain serverDomain,
            ConnectionFactory connectionFactory
    ) {
        this.serverDomain = serverDomain;
        this.factory = connectionFactory;
        this.objectMapper = initObjectMapper();
    }

    public <T extends BasedEvent> MessagePublisher<T> generatePublisher(Topic topic) {
        return factory.generatePublisher(topic, objectMapper);
    }

    public void startSubscribe(
            Map<Topic, SubscribeBehaviour<? extends BasedEvent>> subscribeBehaviourMap
    ) {
        factory.startSubscribe(subscribeBehaviourMap, serverDomain, objectMapper);
    }

    private ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }

}
