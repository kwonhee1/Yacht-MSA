package HooYah.Redis.template;

import HooYah.Redis.RedisValue;
import HooYah.Redis.connection.Connection;
import HooYah.Redis.connection.Pipeline;
import HooYah.Redis.connection.SaveSecond;
import HooYah.Redis.pool.Pool;
import java.util.List;

/*
    set 할 때는 String 값으로
    get 할 때는 RedisValue로 반환
 */
public class RedisTemplate implements Template {

    private final Pool pool;

    public RedisTemplate(Pool pool) {
        this.pool = pool;
    }

    @Override
    public void add(String key, String value, SaveSecond second) {
        try (Connection connection = pool.getConnection()) {
            connection.set(key, value, second);
        }
    }

    @Override
    public void addAll(List<String> keyList, List<String> valueList, SaveSecond second) {
        if(keyList.size() != valueList.size())
            throw new IllegalArgumentException("keyList.size() != valueList.size()");

        try(Pipeline pipeline = pool.getConnection().pipeline()) {
            for (int i = 0; i < keyList.size(); i++)
                pipeline.set(keyList.get(i), valueList.get(i), second);

            pipeline.sync();
        }
    }

    @Override
    public RedisValue get(String key, SaveSecond second) {
        try (Connection connection = pool.getConnection()) {
            String result = connection.get(key, second);
            return new RedisValue(result);
        }
    }

    @Override
    public List<RedisValue> getList(List<String> keyList, SaveSecond second) {
        try (Pipeline pipeline = pool.getConnection().pipeline()) {
            keyList.forEach(key -> pipeline.get(key, second));

            List<String> result = pipeline.sync();

            return result.stream().map(RedisValue::new).toList();
        }
    }

    @Override
    public void remove(String key, SaveSecond second) {
        this.add(key, RedisValue.NULL, second);
    }
}

interface Template {

    void add(String key, String value, SaveSecond second);

    void addAll(List<String> keyList, List<String> valueList, SaveSecond second);

    RedisValue get(String key, SaveSecond second);

    List<RedisValue> getList(List<String> keyList, SaveSecond second);

    void remove(String key, SaveSecond second);

}
