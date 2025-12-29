package HooYah.Redis.connection.inmemory;

import HooYah.Redis.connection.Pipeline;
import HooYah.Redis.connection.SaveSecond;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPipeLine implements Pipeline {

    private final Map<String, String> inMemory;

    private final Map<String, String> add = new HashMap<String, String>();
    private final List<String> result = new ArrayList<String>();

    public InMemoryPipeLine(Map<String, String> inMemory) {
        this.inMemory = inMemory;
    }

    @Override
    public Pipeline set(String key, String value, SaveSecond second) {
        add.put(key, value);
        return this;
    }

    @Override
    public Pipeline get(String key, SaveSecond second) {
        result.add(inMemory.get(key));
        return this;
    }

    @Override
    public List<String> sync() {
        inMemory.putAll(add);
        return result;
    }

    @Override
    public void close() {

    }
}
