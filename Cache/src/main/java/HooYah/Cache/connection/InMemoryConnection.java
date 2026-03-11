package HooYah.Cache.connection;

import HooYah.Cache.pool.Pool;
import HooYah.Cache.pool.InMemoryStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryConnection implements Connection {

    private Pool from;
    private InMemoryStorage storage;

    private final Map<String, String> add = new HashMap<String, String>();
    private final List<String> result = new ArrayList<>();

    private volatile boolean isClosed = false;

    public InMemoryConnection(Pool pool, InMemoryStorage storage) {
        this.from = pool;
        this.storage = storage;
    }

    @Override
    public Connection set(String key, String value, SaveSecond second) {
        if(isClosed)
            throw new ConnectionClosedException(this);

        add.put(key, value);
        return this;
    }

    @Override
    public Connection get(String key, SaveSecond second) {
        if(isClosed)
            throw new ConnectionClosedException(this);

        result.add(storage.get(key, second));
        return this;
    }

    @Override
    public List<String> sync() {
        if(isClosed)
            throw new ConnectionClosedException(this);

        storage.setAll(add, null);
        add.clear();
        return result;
    }

    @Override
    public void close() {
        if(isClosed)
            return;

        isClosed = true;

        if(from != null)
            from.returnResource(this);

        // Storage 참조 해제
        from = null;
        storage = null;
    }
}
