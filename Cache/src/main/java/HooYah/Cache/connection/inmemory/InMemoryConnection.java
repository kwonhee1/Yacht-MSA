package HooYah.Cache.connection.inmemory;

import HooYah.Cache.connection.Connection;
import HooYah.Cache.connection.Pipeline;
import HooYah.Cache.connection.SaveSecond;
import java.util.Map;

public class InMemoryConnection implements Connection {

    private final Pipeline pipeline;

    public InMemoryConnection(Map<String, String> inMemory) {
        this.pipeline = new InMemoryPipeLine(inMemory);
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

    }
}
