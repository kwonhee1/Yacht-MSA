package HooYah.Redis.pool;

import HooYah.Redis.connection.Connection;
import HooYah.Redis.connection.jedis.JedisConnection;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPool implements Pool {

    private redis.clients.jedis.JedisPool pool;

    public JedisPool(String host, int port, String username, String password, int maxConnection) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxConnection);

        pool = new redis.clients.jedis.JedisPool(config, host, port, 2000, username, password);

        try {
            this.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() {
        return new JedisConnection(pool.getResource());
    }

}
