package HooYah.Yacht.conf;

import HooYah.Yacht.Domain;
import HooYah.Yacht.MessageQue;
import HooYah.Yacht.Topic;
import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.CalendarCompleteEvent;
import HooYah.Yacht.event.CreatePartEvent;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.event.NextRepairDateChangedEvent;
import HooYah.Yacht.event.YachtCreateEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.service.PartService;
import HooYah.Yacht.service.RepairService;
import HooYah.Yacht.subscriber.SubscribeBehaviour;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class MessageQueConfig {

    private MessageQue messageQue;

    private final PartService partService;
    private final RepairService repairService;

    public MessageQueConfig(
            @Lazy PartService partService,
            @Lazy RepairService repairService
    ) {
        this.partService = partService;
        this.repairService = repairService;
    }

    @Value("${mq.host}")
    private String host;
    @Value("${mq.port}")
    private int port;
    @Value("${mq.username}")
    private String username;
    @Value("${mq.password}")
    private String password;

    @PostConstruct
    public void init() {
        messageQue = new MessageQue(Domain.PART, ConnectionFactory.redisConnectionFactory(host, port, username, password));

        Map subscribeBehaviour = SubscribeBehaviour.builder()
                .add(Topic.YACHT_DELETE, DeletedEvent.class, (event) -> partService.deleteByYachtId(event.getIdValue(), event.getUserIdValue()))
                .add(Topic.YACHT_CREATE, YachtCreateEvent.class, (event) -> partService.addPartList(event.getIdValue(), event.getPartList(), event.getUserIdValue()))
                .add(Topic.PART_CREATE, CreatePartEvent.class, (event)-> repairService.addRepair(event.getIdValue(), "auto-generate", event.getLastRepairDate(), event.getUserIdValue()))
                .add(Topic.CALENDAR_COMPLETE, CalendarCompleteEvent.class, (event)->repairService.addRepair(event.getPartIdValue(), event.getContent(), event.getCompleteTimeValue(),event.getUserIdValue()))
                .build();

        messageQue.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<DeletedEvent> partDeleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.PART_DELETE);
    }

    @Bean
    public MessagePublisher<CreatePartEvent> partCreateMessagePublisher() {
        return messageQue.generatePublisher(Topic.PART_CREATE);
    }

    @Bean
    public MessagePublisher<NextRepairDateChangedEvent> nextRepairDateChangedMessagePublisher() {
        return messageQue.generatePublisher(Topic.LAST_REPAIR_DATE_CHANGED);
    }

}
