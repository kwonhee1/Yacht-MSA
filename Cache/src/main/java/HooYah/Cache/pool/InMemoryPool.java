package HooYah.Cache.pool;

import HooYah.Cache.connection.Connection;
import HooYah.Cache.connection.InMemoryConnection;
import java.util.ArrayList;
import java.util.List;

public class InMemoryPool implements Pool {

    private final List<Connection> publishedConnections = new ArrayList<>();
    private InMemoryStorage storage = new InMemoryStorage();

    private final static Long WAIT_TIME_MILL = 5000L; // 5초
    private volatile boolean isClosed = false;

    @Override
    public Connection getConnection() {
        Connection connection = new InMemoryConnection(this, storage);
        synchronized (publishedConnections) {
            if(isClosed)
                throw new PoolClosedException(this);
            publishedConnections.add(connection);
        }
        return connection;
    }

    public void returnResource(Connection connection) {
        synchronized (publishedConnections) {
            publishedConnections.remove(connection);
        }
    }

    @Override
    public Pool copyNew() {
        return this;
    }

    @Override
    public void close() {
        isClosed = true;
        closePublishedConnections();
        storage = null;
    }

    private void closePublishedConnections() {
        synchronized (publishedConnections) {
            if(publishedConnections.isEmpty())
                return;
        }

        try {
            Thread.sleep(WAIT_TIME_MILL);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        synchronized (publishedConnections) {
            List<Connection> publishedConnectionsCopy = List.copyOf(publishedConnections);
            for(Connection connection : publishedConnectionsCopy)
                connection.close();
        }
    }
}
