package HooYah.Cache.pool;

import HooYah.Cache.connection.Connection;
import HooYah.Cache.connection.inmemory.InMemoryConnection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryPool implements Pool {

    private final Map<String, String> inMemory = new HashMap<String, String>();

    @Override
    public Connection getConnection() {
        return new InMemoryConnection(inMemory);
    }

}
