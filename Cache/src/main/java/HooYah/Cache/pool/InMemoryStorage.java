package HooYah.Cache.pool;

import HooYah.Cache.connection.SaveSecond;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage {

    private final Map<String, String> inMemory = new ConcurrentHashMap<>();

    public void set(String key, String value, SaveSecond second) {
        inMemory.put(key, value);
    }

    public void setAll(Map<String, String> data, SaveSecond second) {
        inMemory.putAll(data);
    }

    public String get(String key, SaveSecond second) {
        return inMemory.get(key);
    }

    public List<String> getAll(List<String> keys, SaveSecond second) {
        List<String> result = new ArrayList<>();
        for (String key : keys) {
            result.add(inMemory.get(key));
        }
        return result;
    }
}
