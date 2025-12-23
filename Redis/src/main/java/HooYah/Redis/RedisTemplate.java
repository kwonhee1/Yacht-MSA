package HooYah.Redis;

public interface RedisTemplate {

    void add(String key, String value, Long second);
    RedisValue get(String key, Long second);
    void remove(String key);

}
