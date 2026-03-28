package HooYah.Yacht.publisher;

import HooYah.Yacht.Domain;
import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisBusyException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisPublisher <T extends BasedEvent> implements MessagePublisher<T> {

    private final RedisTemplate<String, String> redisTemplate;
    private final Topic topic;
    private final ObjectMapper objectMapper;

    public RedisPublisher(
            RedisTemplate<String, String> redisTemplate,
            Topic topic,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
        this.objectMapper = objectMapper;

        createSubscribeGroup(topic, redisTemplate);
    }

    private void createSubscribeGroup(Topic topic, RedisTemplate<String, String> redisTemplate) {
        for(Domain domain : Domain.values())
            try {
                redisTemplate.opsForStream().createGroup(topic.topic(), ReadOffset.latest(), topic.group(domain));
            } catch (RedisSystemException e) {
                // when subscriber add subscriber group
                if (!(e.getCause() instanceof RedisBusyException))
                    throw e;
            }
    }

    @Override
    public void publish(T message) {
        redisTemplate.opsForStream().add(topic.topic(), Map.of("data", mapper(message)));
    }

    private String mapper(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
