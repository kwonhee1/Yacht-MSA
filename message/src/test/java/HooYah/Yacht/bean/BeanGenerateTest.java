package HooYah.Yacht.bean;

import HooYah.Yacht.Domain;
import HooYah.Yacht.RedisMessageQueSetting;
import HooYah.Yacht.Topic;
import HooYah.Yacht.dto.DeletedEvent;
import HooYah.Yacht.dto.LastRepairChangedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.subscriber.Behaviour;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Configuration
public class BeanGenerateTest {

    private RedisMessageQueSetting setting;

    public BeanGenerateTest() {
        setting = new RedisMessageQueSetting(new ObjectMapper(), Domain.PART);

        Map subscribeBehaviour = Behaviour.builder()
                .add(Topic.YACHT_DELETE, (DeletedEvent event)->System.out.println("delete parts by yacht id"))
                .build();

        setting.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<DeletedEvent> partDeletePublisher() {
        return setting.generatePublisher(Topic.PART_DELETE);
    }

    @Bean
    public MessagePublisher<LastRepairChangedEvent> repairChangedPublisher() {
        return setting.generatePublisher(Topic.PART_INTERVAL_CHANGED);
    }

    @Test
    public void main() {
        partDeletePublisher().publish(new DeletedEvent(1L, 1L));
    }

}
