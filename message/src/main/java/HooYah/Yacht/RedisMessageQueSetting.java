package HooYah.Yacht;

import HooYah.Yacht.dto.BasedEventData;
import HooYah.Yacht.publisher.LogStatePublisher;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.publisher.RedisPublisher;
import HooYah.Yacht.subscriber.RedisSubscriberGenerator;
import HooYah.Yacht.subscriber.Behaviour;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

public class RedisMessageQueSetting {

    private final RedisConnectionFactory factory;
    private final ObjectMapper objectMapper;

    private final Domain serverDomain;

    private final RedisTemplate<String, BasedEventData> redisTemplate;

    public RedisMessageQueSetting(
            ObjectMapper objectMapper,
            Domain serverDomain
    ) {
        this.factory = new LettuceConnectionFactory(); // need connection pool impl :: JedisConnectionFactory or LettuceConnectionFactory
        // 일단 그냥 편한거 쓰고 나중에 Redis.jar과 connection pool 병합 필요하면 그때 가서 처리하는 걸로
        this.objectMapper = objectMapper;
        this.serverDomain = serverDomain;
        this.redisTemplate = generateRedisTemplate(factory);
    }

    private RedisTemplate<String, BasedEventData> generateRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, BasedEventData> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    public <T extends BasedEventData> MessagePublisher<T> generatePublisher(Topic topic) {
        try {
            return new RedisPublisher<T>(redisTemplate, topic, topic.eventClass());
        } catch (IllegalArgumentException e) {
            // thrown IllegalArgument when Redis Pool is not initialized.
            // java.lang.IllegalArgumentException: template not initialized; call afterPropertiesSet() before using it
            return new LogStatePublisher(topic);
        }

    }

    public void startSubscribe(
            Map<Topic, Behaviour> subscribeBehaviourMap
    ) {
        try {
            RedisSubscriberGenerator.streamListenerContainer(factory, subscribeBehaviourMap, serverDomain, objectMapper);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
