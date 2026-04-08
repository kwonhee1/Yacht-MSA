package HooYah.Cache.pool;

import HooYah.Cache.connection.Connection;
import HooYah.Cache.connection.JedisConnection;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

public class JedisPool implements Pool {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final int maxConnection;
    private redis.clients.jedis.JedisPool pool;

    public JedisPool(String host, int port, String username, String password, int maxConnection) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.maxConnection = maxConnection;

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxConnection);

        pool = new redis.clients.jedis.JedisPool(config, host, port, 2000, username, password);

        try (Connection connection = getConnection()) {
            // if can not connect by host, port :: throws JedisConnectionException :: extends JedisException
            // if id password not correct :: throws JedisDataException :: extends JedisException
        } catch (ConnectFailException e) {
            pool.close();
            throw e;
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return new JedisConnection(pool.getResource());
        } catch (JedisException e) {
            throw new ConnectFailException(e);
        }
    }

    @Override
    public void returnResource(Connection connection) {
        // nothing to do
        // when Jedis.close(), already return Resource to JedisPool by JedisLibrary
    }

    @Override
    public Pool copyNew() {
        return new JedisPool(host, port, username, password, maxConnection);
    }

    @Override
    public void close() {
        pool.close();
    }

}

