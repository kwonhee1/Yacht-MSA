package HooYah.Yacht.messageque.publisher;

import HooYah.Yacht.messageque.Topic;
import HooYah.Yacht.part.dto.response.PartDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Publisher {

    private final RedisTemplate<String, Long> redisTemplate;
    private final ChannelTopic partDeleteTopic;

    public void publishPartDelete(Long partId) {
        redisTemplate.opsForStream().add(Record.of(partId));
        redisTemplate.convertAndSend(partDeleteTopic.getTopic(), partId);
        redisTemplate.opsForStream().add(
                StreamRecords.objectBacked(Map.of("value", partId))
                        .withStreamKey(Topic.PART_DELETE.topic())
        );
    }

}
