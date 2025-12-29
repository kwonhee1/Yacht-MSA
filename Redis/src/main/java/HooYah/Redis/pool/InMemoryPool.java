package HooYah.Redis.pool;

import HooYah.Redis.connection.Connection;
import HooYah.Redis.connection.inmemory.InMemoryConnection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryPool implements Pool {

    private static final Map<String, String> inMemory = new HashMap<String, String>();

    @Override
    public Connection getConnection() {
        return new InMemoryConnection(inMemory);
    }

}
