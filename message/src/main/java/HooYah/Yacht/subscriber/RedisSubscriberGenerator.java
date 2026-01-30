package HooYah.Yacht.subscriber;

import HooYah.Yacht.Domain;
import HooYah.Yacht.dto.BasedEventData;
import HooYah.Yacht.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class RedisSubscriberGenerator {

    /*
        Generate StreamMessageListenerContainer
        @Param connectionFactory
        @Param subscribeBehaviours : Map<Topic :: subscribe Topic, Behaviour :: when Topic message receive>
        @Param domain : server domain used at redis consumer group
        @Param ObjectMapper : used at convert received message to EventClass
     */
    public static StreamMessageListenerContainer streamListenerContainer(
            RedisConnectionFactory connectionFactory,
            Map<Topic, Behaviour> subscribeBehaviourMap,
            Domain domain,
            ObjectMapper objectMapper
    ) {
        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(2)) // long polling
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        for(Topic topic : subscribeBehaviourMap.keySet()) {
            container.receive(
                    Consumer.from(topic.group(domain), "consumer-1"),
                    StreamOffset.create(topic.group(domain), ReadOffset.lastConsumed()),
                    generateStreamListener(topic.eventClass(), subscribeBehaviourMap.get(topic), objectMapper)
            );
        }

        container.start();
        return container;
    }

    private static <T extends BasedEventData> StreamListener<String, MapRecord<String, String, String>> generateStreamListener (
            Class<T> eventClass, Behaviour behaviour,
            ObjectMapper objectMapper
    ) {
        return new  StreamListener<String, MapRecord<String, String, String>>() {
            @Override
            public void onMessage(MapRecord<String, String, String> mapRecord) {
                Map<String, String> message = mapRecord.getValue();

                T event = objectMapper.convertValue(message, eventClass);

                behaviour.subscribe(event);
            }
        };
    }

}
