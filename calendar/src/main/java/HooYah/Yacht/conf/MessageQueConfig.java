package HooYah.Yacht.conf;

import HooYah.Yacht.Domain;
import HooYah.Yacht.MessageQue;
import HooYah.Yacht.Topic;
import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.CalendarSuccessEvent;
import HooYah.Yacht.event.CreateEvent;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.event.LastRepairChangedEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.service.AlarmService;
import HooYah.Yacht.service.CalendarService;
import HooYah.Yacht.subscriber.Behaviour;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MessageQueConfig {

    private final CalendarService calendarService;
    private final AlarmService alarmService;
    private MessageQue messageQue;

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
        messageQue = new MessageQue(Domain.CALENDAR, ConnectionFactory.redisConnectionFactory(host, port, username, password));

        Map subscribeBehaviour = Behaviour.builder()
                .add(Topic.USER_CREATE, (CreateEvent event) -> alarmService.saveToken(event.getId(), event.getToken()))
                .add(Topic.USER_DELETE, (DeletedEvent event) -> calendarService.deleteUser(event))
                .add(Topic.YACHT_DELETE, (DeletedEvent event) -> calendarService.deleteYacht(event))
                .add(Topic.PART_DELETE, (DeletedEvent event) -> calendarService.deletePart(event))
                .add(Topic.PART_INTERVAL_CHANGED, (LastRepairChangedEvent event) -> calendarService.updatePartInterval(event))
                .add(Topic.REPAIR_ADD, (LastRepairChangedEvent event) -> calendarService.addRepair(event))
                .build();

        messageQue.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<DeletedEvent> calendarDeleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.CALENDAR_DELETE);
    }

    @Bean
    public MessagePublisher<CalendarSuccessEvent> calendarSuccessMessagePublisher() {
        return messageQue.generatePublisher(Topic.CALENDAR_SUCCESS);
    }

}
