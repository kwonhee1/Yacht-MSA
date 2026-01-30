package HooYah.Yacht.conf;

import HooYah.Yacht.Domain;
import HooYah.Yacht.RedisMessageQueSetting;
import HooYah.Yacht.Topic;
import HooYah.Yacht.dto.CalendarSuccessEvent;
import HooYah.Yacht.dto.DeletedEvent;
import HooYah.Yacht.dto.LastRepairChangedEvent;
import HooYah.Yacht.part.service.PartService;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.repair.service.RepairService;
import HooYah.Yacht.subscriber.Behaviour;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisQueConfig {

    private RedisMessageQueSetting setting;

    public RedisQueConfig(
            ObjectMapper objectMapper,
            PartService partService,
            RepairService repairService
    ) {
        setting = new RedisMessageQueSetting(objectMapper, Domain.PART);

        Map subscribeBehaviourMap = Behaviour.builder()
                .add(Topic.YACHT_DELETE, (DeletedEvent event)->partService.deletePartByYachtId(event.getId()))
                .add(Topic.CALENDAR_SUCCESS, (CalendarSuccessEvent event)->repairService.addRepair(event.getPartId(), event.getReview(), event.getStartTime(), event.getUserId()))
                .build();

        setting.startSubscribe(subscribeBehaviourMap);
    }

    @Bean
    public MessagePublisher<DeletedEvent> partDeletePublisher() {
        return setting.generatePublisher(Topic.PART_DELETE);
    }

    @Bean
    public MessagePublisher<LastRepairChangedEvent> repairChangedPublisher() {
        return setting.generatePublisher(Topic.PART_INTERVAL_CHANGED);
    }

}
