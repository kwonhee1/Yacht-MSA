package HooYah.Yacht.publisher;

import HooYah.Yacht.Domain;
import HooYah.Yacht.dto.BasedEventData;
import HooYah.Yacht.Topic;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisPublisher <T extends BasedEventData> implements MessagePublisher<T> {

    private final RedisTemplate<String, BasedEventData> redisTemplate;
    private final Topic topic;

    public RedisPublisher(
            RedisTemplate<String, BasedEventData> redisTemplate,
            Topic topic,
            Class<T> eventClass
    ) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;

        createSubscribeGroup(topic, redisTemplate);
    }

    private void createSubscribeGroup(Topic topic, RedisTemplate<String, BasedEventData> redisTemplate) {
        for(Domain domain : Domain.values())
            redisTemplate.opsForStream()
                    .createGroup(topic.topic(), Topic.PART_DELETE.group(domain));
    }

    @Override
    public void publish(T message) {
        topic.validateEventType(message);
        redisTemplate.opsForStream().add(StreamRecords.objectBacked(message).withStreamKey(topic.topic()));
    }

}
