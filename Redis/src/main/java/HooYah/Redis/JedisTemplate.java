package HooYah.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.SetParams;

public class JedisTemplate implements RedisTemplate {

    private final ConnectionPool connectionPool;

    public JedisTemplate(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void add(String key, String value, Long second) {
        try (Jedis jedis = connectionPool.getConnection()) {
            jedis.set(key, value, new SetParams().ex(second));
        }
    }

    @Override
    public RedisValue get(String key, Long second) {
        try (Jedis jedis = connectionPool.getConnection()) {
            String str = jedis.getEx(key, new GetExParams().ex(second));
            return new RedisValue(str);
        }
    }

    @Override
    public void remove(String key) {
        try (Jedis jedis = connectionPool.getConnection()) {
            jedis.del(key);
        }
    }

}
