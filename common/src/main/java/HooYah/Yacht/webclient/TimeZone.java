package HooYah.Yacht.webclient;

public enum TimeZone {

    SEOUL("Asia/Seoul"),
    JAPAN("Asia/Tokyo")
    ;

    private java.util.TimeZone timeZone;

    TimeZone(String timeZone) {
        this.timeZone = java.util.TimeZone.getTimeZone(timeZone);
    }

    public java.util.TimeZone getTimeZone() {
        return timeZone;
    }

}
