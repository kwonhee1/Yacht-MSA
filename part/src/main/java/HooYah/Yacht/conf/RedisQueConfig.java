package HooYah.Yacht.conf;

import HooYah.Yacht.messageque.Domain;
import HooYah.Yacht.messageque.Topic;
import HooYah.Yacht.messageque.subscriber.SubscriberContainerConfig.OnMessage;
import HooYah.Yacht.messageque.subscriber.SubscriberContainerConfig;
import HooYah.Yacht.part.service.PartService;
import HooYah.Yacht.repair.service.RepairService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

@Configuration
public class RedisQueConfig {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PartService partService;
    private final RepairService repairService;

    public RedisQueConfig(
            RedisConnectionFactory factory,
            PartService partService,
            RepairService repairService
    ) {
        this.redisTemplate = createRedisTemplate(factory);
        this.partService = partService;
        this.repairService = repairService;
        initRedisGroups();
    }

    private void initRedisGroups() {
        createAllGroup(Topic.PART_DELETE);
        createAllGroup(Topic.PART_CHANGE_INTERVAL);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        return redisTemplate;
    }

    @Bean
    public StreamMessageListenerContainer streamMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        Map<Topic, OnMessage> topicMap = Map.of(
                Topic.YACHT_DELETE, (yachtId)-> partService.deletePartByYachtId(yachtId) ,
                Topic.CALENDAR_SUCCESS, (partId)-> repairService.addRepair()
        );
        return new SubscriberContainerConfig(connectionFactory, , Domain.PART).streamListenerContainer();
    }

    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    private void createAllGroup(Topic topic) {
        for(Domain domain : Domain.values())
            redisTemplate.opsForStream()
                    .createGroup(topic.topic(), Topic.PART_DELETE.group(domain));
    }

}
