package HooYah.Yacht.messageque;

public enum Topic {
    // send
    PART_DELETE("part.delete"),
    PART_CHANGE_INTERVAL("part.interval"),
    // receive
    YACHT_DELETE("yacht.delete"),
    CALENDAR_SUCCESS("calendar.success"),
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
