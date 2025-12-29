package HooYah.Redis;

import java.util.Optional;

public class RedisValue {

    public final static String NULL = "NULL";

    private String value;
    private Status status;

    public RedisValue(String value) {
        if(value == null || value.isEmpty())
            init(null, Status.UN_KNOWN);
        else if(value.equals(NULL))
            init(null, Status.NULL);
        else
            init(value, Status.EXIST);
    }

    private void init(String value, Status status) {
        this.value = value;
        this.status = status;
    }

    public String get() {
        return value;
    }

    public boolean isUnKnown() {
        return status == Status.UN_KNOWN;
    }

    public boolean isNull() {
        return status == Status.NULL;
    }

    public boolean hasValue() {
        return status == Status.EXIST;
    }

    enum Status {
        EXIST,
        NULL,
        UN_KNOWN // need select
    }
}
