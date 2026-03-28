package HooYah.Yacht;

public enum Topic {
    //user
    USER_DELETE("user.delete"),
    USER_CREATE("user.create"),

    //yacht
    YACHT_DELETE("yacht.delete"),
    YACHT_CREATE("yacht.create"),

    //part
    PART_DELETE("part.delete"),
    PART_CREATE("part.create"),
    LAST_REPAIR_DATE_CHANGED("part.lastRepair"),

    // repair
    REPAIR_CREATE("repair.create"),

    // calendar
    CALENDAR_CREATE("calendar.create"),
    CALENDAR_DELETE("calendar.delete"),
    CALENDAR_COMPLETE("calendar.complete"),

    // alarm
    ALARM_DELETE("alarm.delete"),
    ;

    private String topic;

    Topic(String topic) {
        this.topic = topic;
    }

    public String topic() {
        return topic;
    }

    public String group(Domain domain) {
        return topic + "_group_" + domain.name();
    }

}
