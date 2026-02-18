package HooYah.Cache;

public class RedisException extends RuntimeException {
    public RedisException(String message) {
        super("redis error : " + message);
    }
}
