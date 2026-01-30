package HooYah.Yacht;

import HooYah.Yacht.event.BasedEvent;
import HooYah.Yacht.event.CalendarSuccessEvent;
import HooYah.Yacht.event.CreateEvent;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.event.LastRepairChangedEvent;

public enum Topic {
    //user
    USER_DELETE("user.delete", DeletedEvent.class),
    USER_CREATE("user.create", CreateEvent.class),

    //yacht
    YACHT_DELETE("yacht.delete", DeletedEvent.class),

    //part
    PART_DELETE("part.delete", DeletedEvent.class),
    PART_INTERVAL_CHANGED("part.interval", LastRepairChangedEvent.class), // part changed interval

    // repair
    REPAIR_ADD("repair.add", LastRepairChangedEvent.class),

    // calendar
    CALENDAR_DELETE("calendar.delete", DeletedEvent.class),
    CALENDAR_SUCCESS("calendar.success", CalendarSuccessEvent.class),
    ;

    private String topic;
    private Class<? extends BasedEvent> eventClass;

    Topic(String topic, Class eventClass) {
        this.topic = topic;
        this.eventClass = eventClass;
    }

    public String topic() {
        return topic;
    }

    public String group(Domain domain) {
        return topic + "_group_" + domain.name();
    }

    public Class<? extends BasedEvent> eventClass() {
        return eventClass;
    }

    public void validateEventType(Object event) {
        if (event.getClass() != eventClass)
            throw new WrongEventClassException(this, event.getClass());
    }

}
