package HooYah.Cache.connection;

import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.SetParams;

public class JedisConnection implements Connection {

    private final Jedis jedis;

    private final Pipeline pipeline;
    private final List<Response<String>> resultList = new ArrayList<>();

    public JedisConnection(Jedis jedis) {
        this.jedis = jedis;
        pipeline = new Pipeline(jedis);
    }

    @Override
    public Connection set(String key, String value, SaveSecond second) {
        pipeline.set(key, value, new SetParams().ex(second.get()));
        return this;
    }

    @Override
    public Connection get(String key, SaveSecond second) {
        resultList.add(pipeline.getEx(key, new GetExParams().ex(second.get())));
        return this;
    }

    @Override
    public List<String> sync() {
        pipeline.sync();
        return resultList.stream().map(Response::get).toList();
    }

    @Override
    public void close() {
        pipeline.close();
        jedis.close(); // return resource to Pool<Jedis>
    }

}
