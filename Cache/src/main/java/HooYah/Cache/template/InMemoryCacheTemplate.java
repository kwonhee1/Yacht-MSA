package HooYah.Cache.template;

import HooYah.Cache.connection.Connection;
import HooYah.Cache.connection.SaveSecond;
import HooYah.Cache.pool.InMemoryPool;
import HooYah.Cache.pool.Pool;
import java.util.List;
import java.util.function.Function;

public class InMemoryCacheTemplate implements Template {

    private final InMemoryPool inMemoryPool;

    public InMemoryCacheTemplate(InMemoryPool inMemoryPool) {
        this.inMemoryPool = inMemoryPool;
    }

    @Override
    public void add(String key, String value, SaveSecond second) {
        execute(connection -> {
            connection.set(key, value, second).sync();
            return null;
        });
    }

    @Override
    public void addAll(List<String> keyList, List<String> valueList, SaveSecond second) {
        if(keyList.size() != valueList.size())
            throw new IllegalArgumentException("keyList.size() != valueList.size()");

        execute(connection -> {
            for(int i = 0; i < keyList.size(); i++)
                connection.set(keyList.get(i), valueList.get(i), second);

            connection.sync();
            return null;
        });
    }

    @Override
    public String get(String key, SaveSecond second) {
        return execute(connection -> connection.get(key, second).sync().getFirst());
    }

    @Override
    public List<String> getList(List<String> keyList, SaveSecond second) {
        return execute(connection -> {
            keyList.forEach(key -> connection.get(key, second));
            return connection.sync();
        });
    }

    private <R> R execute(Function<Connection, R> operation) {
        try (Connection connection = inMemoryPool.getConnection()) {
            return operation.apply(connection);
        }
    }

    @Override
    public void reconnect() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        inMemoryPool.close();
    }

}
