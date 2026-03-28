package HooYah.Cache.template;

import HooYah.Cache.connection.Connection;
import HooYah.Cache.connection.SaveSecond;
import HooYah.Cache.pool.ConnectFailException;
import HooYah.Cache.pool.InMemoryPool;
import HooYah.Cache.pool.JedisPool;
import HooYah.Cache.pool.Pool;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/*
    Redis / InMemory를 사용하기 위한 class
*/
public class CacheTemplate implements Template {

    private volatile Pool pool;

    public CacheTemplate(Pool pool) {
        this.pool = pool;
    }

    @Override
    public void add(String key, String value, SaveSecond second) {
        execute((Function<Connection, List<String>>) connection -> connection.set(key, value, second).sync());
    }

    @Override
    public void addAll(List<String> keyList, List<String> valueList, SaveSecond second) {
        if(keyList.size() != valueList.size())
            throw new IllegalArgumentException("keyList.size() != valueList.size()");

        execute(connection -> {
            for(int i = 0; i < keyList.size(); i++)
                connection.set(keyList.get(i), valueList.get(i), second);

            connection.sync();
        });
    }

    @Override
    public String get(String key, SaveSecond second) {
        return execute((Function<Connection, String>) connection -> connection.get(key, second).sync().getFirst());
    }

    @Override
    public List<String> getList(List<String> keyList, SaveSecond second) {
        return execute(connection -> {
            keyList.forEach(key -> connection.get(key, second));

            return connection.sync();
        });
    }

    private <R> R execute(Function<Connection, R> operation) {
        try (Connection connection = pool.getConnection()) {
            return operation.apply(connection);
        } catch (ConnectFailException e) {
            switchToInMemory();
            try (Connection connection = pool.getConnection()) {
                return operation.apply(connection);
            }
        }
    }

    private void execute(Consumer<Connection> operation) {
        try (Connection connection = pool.getConnection()) {
            operation.accept(connection);
        } catch (ConnectFailException e) {
            switchToInMemory();
            try (Connection connection = pool.getConnection()) {
                operation.accept(connection);
            }
        }
    }

    private synchronized void switchToInMemory() {
        if (!(pool instanceof JedisPool))
            return;

        try { pool.close(); } catch (Exception ignored) {}
        pool = new InMemoryPool();
    }

    @Override
    public void close() {
        pool.close();
        this.pool = null;
    }

}
