package HooYah.Cache.connection;

import java.util.List;

public interface Connection extends AutoCloseable {

    Connection set(String key, String value, SaveSecond second);
    Connection get(String key, SaveSecond second);

    List<String> sync();

    @Override
    void close();

}
