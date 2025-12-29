package HooYah.Redis.connection;

import java.util.List;

public interface Pipeline extends AutoCloseable {
    Pipeline set(String key, String value, SaveSecond second);
    Pipeline get(String key, SaveSecond second);

    List<String> sync();

    @Override
    void close();

}
