package HooYah.Cache.connection.jedis;

import HooYah.Cache.connection.Connection;
import HooYah.Cache.connection.Pipeline;
import HooYah.Cache.connection.SaveSecond;
import redis.clients.jedis.Jedis;

public class JedisConnection implements Connection {

    private final Pipeline pipeline;

    public JedisConnection(Jedis jedis) {
        this.pipeline = new JedisPipeline(jedis);
    }

    @Override
    public Pipeline pipeline() {
        return pipeline;
    }

    @Override
    public void set(String key, String value, SaveSecond second) {
        pipeline.set(key, value, second).sync();
    }

    @Override
    public String get(String key, SaveSecond second) {
        return pipeline.get(key, second).sync().getFirst();
    }

    @Override
    public void close() {
        pipeline.close();
    }

}
