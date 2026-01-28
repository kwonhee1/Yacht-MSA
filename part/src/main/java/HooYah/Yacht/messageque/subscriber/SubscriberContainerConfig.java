package HooYah.Yacht.messageque.subscriber;

import HooYah.Yacht.messageque.Domain;
import HooYah.Yacht.messageque.Topic;
import java.time.Duration;
import java.util.Map;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

public class SubscriberContainerConfig {

    private final RedisConnectionFactory connectionFactory;
    private final Map<Topic, OnMessage> subscribers;
    private final Domain domain;

    public SubscriberContainerConfig(
            RedisConnectionFactory connectionFactory,
            Map<Topic, OnMessage> subscribers,
            Domain domain
    ) {
        this.connectionFactory = connectionFactory;
        this.subscribers = subscribers;
        this.domain = domain;
    }

    private StreamListener<String, MapRecord<String, String, String>> generateStreamListener (OnMessage onMessage) {
        return new  StreamListener<String, MapRecord<String, String, String>>() {
            @Override
            public void onMessage(MapRecord<String, String, String> mapRecord) {
                Long value = Long.parseLong(mapRecord.getValue().get("value"));
                onMessage.onMessage(value);
            }
        };
    }

    public interface OnMessage {
        void onMessage(Long id);
    }


    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamListenerContainer() {
        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(2)) // long polling
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        for(Topic topic : subscribers.keySet()) {
            container.receive(
                    Consumer.from(topic.group(domain), "consumer-1"),
                    StreamOffset.create(topic.group(domain), ReadOffset.lastConsumed()),
                    generateStreamListener(subscribers.get(topic))
            );
        }

        container.start();
        return container;
    }

}
