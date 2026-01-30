package HooYah.Yacht;

import HooYah.Yacht.dto.CalendarSuccessEvent;
import HooYah.Yacht.dto.DeletedEvent;
import HooYah.Yacht.dto.LastRepairChangedEvent;

public enum Topic {
    //user
    USER_DELETE("user.delete", DeletedEvent.class),

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
    private Class eventClass;

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

    public Class eventClass() {
        return eventClass;
    }

    public void validateEventType(Object event) {
        if (event.getClass() != eventClass)
            throw new WrongEventClassException(this, event.getClass());
    }

}
