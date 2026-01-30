package HooYah.Yacht.connectionfactory;

import HooYah.Yacht.Domain;
import HooYah.Yacht.Topic;
import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.publisher.RedisPublisher;
import HooYah.Yacht.subscriber.Behaviour;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

public class RedisConnectionFactory implements ConnectionFactory {

    private boolean isRunning = false;
    private final LettuceConnectionFactory lettuceConnectionFactory;
    private final RedisTemplate<String, String> redisTemplate;

    public RedisConnectionFactory(
            String host,
            int port,
            String user,
            String password
    ) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setUsername(user);
        config.setPassword(password);

        lettuceConnectionFactory = new LettuceConnectionFactory(config);
        redisTemplate = generateRedisTemplate(lettuceConnectionFactory);

        startLettuceConnectionFactory(lettuceConnectionFactory);

        isRunning = true;
        redisTemplate.opsForValue().set("key", "value");
    }

    private RedisTemplate<String, String> generateRedisTemplate(org.springframework.data.redis.connection.RedisConnectionFactory factory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private void startLettuceConnectionFactory(LettuceConnectionFactory connectionFactory) {
        connectionFactory.start();

        // todo : edit check start method (기다리는 방법 -> 다른 방법 필요!)
        int waitCount = 0;
        try {
            while (!connectionFactory.isRunning() && waitCount < 20) {
                Thread.sleep(500L);
                waitCount++;
            }

            if(!connectionFactory.isRunning())
                throw new RuntimeException("Lettuce connection fail");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public <T extends BasedEvent> MessagePublisher<T> generatePublisher(Topic topic, ObjectMapper objectMapper) {
        return new RedisPublisher<T>(redisTemplate, topic, objectMapper);
    }

    @Override
    public void startSubscribe(
            Map<Topic, Behaviour<? extends BasedEvent>> subscribeBehaviourMap,
            Domain serverDomain,
            ObjectMapper objectMapper // todo : ObjectMapper 책임 분리 필요
    ) {
        // todo : 함수, class 분리 필요
        Set<Topic> topicSet = subscribeBehaviourMap.keySet();

        // check stream
        topicSet
                .stream()
                .filter(topic -> !redisTemplate.hasKey(topic.topic()))
                .forEach(topic -> redisTemplate.opsForStream().createGroup(topic.topic(), ReadOffset.latest(), topic.group(serverDomain)));

        // check group
        // key가 있으면 group 또한 있다고 가정한다 (현재 코드에서 key와 group을 동시에 생성함, key생성 코드가 여기 한 곳임)
//        topicSet
//                .stream()
//                .forEach(topic -> {
//                    try {
//                        redisTemplate.opsForStream().createGroup(topic.topic(), ReadOffset.latest(), topic.group(domain));
//                    } catch (RedisSystemException e) {
//                        if (!(e.getCause() instanceof RedisBusyException))
//                            throw e;
//                    }
//                });

        // make Container
        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(2)) // long polling
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(lettuceConnectionFactory, options);

        // add receiver
        for(Topic topic : subscribeBehaviourMap.keySet()) {
            container.receive(
                    Consumer.from(topic.group(serverDomain), "consumer-" + UUID.randomUUID()),
                    StreamOffset.create(topic.topic(), ReadOffset.lastConsumed()),
                    new CustomListener(serverDomain, objectMapper, subscribeBehaviourMap.get(topic), redisTemplate, topic)
            );
        }

        container.start();

        // todo : edit check start method (기다리는 방법 -> 다른 방법 필요!)
        int waitCount = 0;
        try {
            while (!container.isRunning() && waitCount < 20) {
                Thread.sleep(500L);
                waitCount++;
            }

            if(!container.isRunning())
                throw new RuntimeException("Redis SubScriber Container run fail");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @RequiredArgsConstructor
    private class CustomListener implements StreamListener<String, MapRecord<String, String, String>> {

        private final Domain domain;
        private final ObjectMapper objectMapper;
        private final Behaviour behaviour;
        private final RedisTemplate<String, String> redisTemplate;
        private final Topic topic;

        @Override
        public void onMessage(MapRecord<String, String, String> mapRecord) {
            // MapRecord<Stream Name, Key, Value>
            Map<String, String> message = mapRecord.getValue();

            BasedEvent event = objectMapper.convertValue(message, topic.eventClass());

            behaviour.subscribe(event);

            redisTemplate.opsForStream().acknowledge(topic.group(domain), mapRecord);
        }
    }

}
