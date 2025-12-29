package HooYah.Redis.connection.jedis;

import HooYah.Redis.connection.Pipeline;
import HooYah.Redis.connection.SaveSecond;
import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.SetParams;

public class JedisPipeline implements Pipeline {

    private final redis.clients.jedis.Pipeline pipeline;

    private final List<Response<String>> resultList = new ArrayList<>();

    public JedisPipeline(Jedis jedis) {
        this.pipeline = jedis.pipelined();
    }

    @Override
    public Pipeline set(String key, String value, SaveSecond second) {
        pipeline.set(key, value, new SetParams().ex(second.get()));
        return this;
    }

    @Override
    public Pipeline get(String key, SaveSecond second) {
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
    }
}
