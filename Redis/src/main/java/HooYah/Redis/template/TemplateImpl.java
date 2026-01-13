package HooYah.Redis.template;

import HooYah.Redis.connection.Connection;
import HooYah.Redis.connection.Pipeline;
import HooYah.Redis.connection.SaveSecond;
import HooYah.Redis.pool.Pool;
import java.util.List;

/*
    Redis / InMemory를 사용하기 위한 class, Connection을 좀 더 편리하게 사용하기 위함
 */
public class TemplateImpl implements Template {

    private final Pool pool;

    public TemplateImpl(Pool pool) {
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
    public String get(String key, SaveSecond second) {
        try (Connection connection = pool.getConnection()) {
            return connection.get(key, second);
        }
    }

    @Override
    public List<String> getList(List<String> keyList, SaveSecond second) {
        try (Pipeline pipeline = pool.getConnection().pipeline()) {
            keyList.forEach(key -> pipeline.get(key, second));

            return pipeline.sync();
        }
    }

}

interface Template {

    void add(String key, String value, SaveSecond second);

    void addAll(List<String> keyList, List<String> valueList, SaveSecond second);

    String get(String key, SaveSecond second);

    List<String> getList(List<String> keyList, SaveSecond second);

}
