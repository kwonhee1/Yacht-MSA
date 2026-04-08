package HooYah.Cache.template;

import HooYah.Cache.connection.Connection;
import HooYah.Cache.connection.SaveSecond;
import HooYah.Cache.pool.ConnectFailException;
import HooYah.Cache.pool.InMemoryPool;
import HooYah.Cache.pool.Pool;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/*
    Redis / InMemory use template
*/
public class CacheTemplate implements Template {

    private Pool pool;
    private InMemoryPool inMemoryPool;

    private final AtomicBoolean isInMemory = new AtomicBoolean(false);
    private final AtomicBoolean readWriteAccess = new AtomicBoolean(true);

    public CacheTemplate(Pool pool) {
        this.pool = pool;
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
        // todo : check get access

        Pool currentPool = getCurrentPool();

        try (Connection connection = currentPool.getConnection()) {
            return operation.apply(connection);
        } catch (ConnectFailException e) {
            if(isInMemory.get())
                throw e; // prevent 무한 loop (if InMemory throws ConnectionFileException -> 무한 루프 발생 가능 (단순 예방을 위한 코드입니다)

            switchToInMemory();
            return execute(operation); // retry
        }
    }

    private Pool getCurrentPool() {
        if (isInMemory.get()) {
            return inMemoryPool;
        }
        return pool;
    }

    private void blockAccess() {
        readWriteAccess.set(false);
    }

    private void allowAccess() {
        readWriteAccess.set(true);
    }

    private synchronized void switchToInMemory() {
        if (isInMemory.get())
            return;

        blockAccess();
        inMemoryPool = new InMemoryPool();
        isInMemory.set(true);
        allowAccess();
    }

    @Override
    public synchronized void reconnect() {
        Pool newPool = pool.copyNew();
        Pool oldPool = getCurrentPool();

        // switch
        blockAccess();
        pool = newPool;
        isInMemory.set(false);
        allowAccess();

        // close pool
        if(inMemoryPool != null) {
            inMemoryPool.close();
            inMemoryPool = null;
        }
        if(oldPool != null)
            oldPool.close();
    }

    @Override
    public void close() {
        if (inMemoryPool != null)
            inMemoryPool.close();

        if(pool != null)
            pool.close();
    }

}
