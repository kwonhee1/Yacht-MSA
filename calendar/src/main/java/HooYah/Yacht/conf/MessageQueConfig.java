package HooYah.Yacht.conf;

import HooYah.Yacht.Domain;
import HooYah.Yacht.MessageQue;
import HooYah.Yacht.Topic;
import HooYah.Yacht.connectionfactory.ConnectionFactory;
import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.event.CalendarCompleteEvent;
import HooYah.Yacht.event.CreateEvent;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.event.NextRepairDateChangedEvent;
import HooYah.Yacht.event.UserCreateEvent;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.service.AlarmService;
import HooYah.Yacht.service.CalendarAlarmAutoGeneratorService;
import HooYah.Yacht.service.CalendarService;
import HooYah.Yacht.subscriber.SubscribeBehaviour;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class MessageQueConfig {

    private final CalendarService calendarService;
    private final AlarmService alarmService;
    private final CalendarAlarmAutoGeneratorService calendarAlarmAutoGeneratorService;

    private MessageQue messageQue;

    public MessageQueConfig(
            @Lazy CalendarService calendarService,
            @Lazy AlarmService alarmService,
            @Lazy CalendarAlarmAutoGeneratorService calendarAlarmAutoGeneratorService
    ) {
        this.calendarService = calendarService;
        this.alarmService = alarmService;
        this.calendarAlarmAutoGeneratorService = calendarAlarmAutoGeneratorService;
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
        messageQue = new MessageQue(Domain.CALENDAR, ConnectionFactory.redisConnectionFactory(host, port, username, password));

        Map<Topic, SubscribeBehaviour<? extends BasedEvent>> subscribeBehaviour = SubscribeBehaviour.builder()
                .add(Topic.USER_CREATE, UserCreateEvent.class, (event) -> alarmService.saveToken(event.getUserIdValue(), event.getToken()))
                .add(Topic.USER_DELETE, DeletedEvent.class, // delete user alarm token
                        (DeletedEvent event) -> {alarmService.deleteUserTokenByUserId(event.getUserIdValue()); calendarService.deleteCalendarUserByUserId(event.getUserIdValue());})
                .add(Topic.YACHT_DELETE, DeletedEvent.class, // delete alarm, calendar
                        (DeletedEvent event) -> {calendarService.deleteByYachtId(event.getIdValue()); alarmService.deleteAlarmByYachtId(event.getIdValue());})
                .add(Topic.PART_DELETE, DeletedEvent.class, (DeletedEvent event) -> calendarService.deleteByPartId(event.getIdValue()))
                // last repair create, last repair update, part interval update
                .add(Topic.LAST_REPAIR_DATE_CHANGED, NextRepairDateChangedEvent.class, (event)->calendarAlarmAutoGeneratorService.generate(event.getIdValue(), event.getYachtId(), event.getNextRepairDate()))
                .build();

        messageQue.startSubscribe(subscribeBehaviour);
    }

    @Bean
    public MessagePublisher<CreateEvent> calendarCreateMessagePublisher() {
        return messageQue.generatePublisher(Topic.CALENDAR_CREATE);
    }

    @Bean
    public MessagePublisher<DeletedEvent> calendarDeleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.CALENDAR_DELETE);
    }

    @Bean
    public MessagePublisher<DeletedEvent> alarmDeleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.ALARM_DELETE);
    }

    @Bean
    public MessagePublisher<CalendarCompleteEvent> calendarCompleteMessagePublisher() {
        return messageQue.generatePublisher(Topic.CALENDAR_COMPLETE);
    }

}
