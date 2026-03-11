package HooYah.Cache.template;

import HooYah.Cache.connection.Connection;
import HooYah.Cache.connection.SaveSecond;
import HooYah.Cache.pool.Pool;
import java.util.List;

/*
    Redis / InMemory를 사용하기 위한 class
*/
public class CacheTemplate implements Template {

    private Pool pool;

    public CacheTemplate(Pool pool) {
        this.pool = pool;
    }

    @Override
    public void add(String key, String value, SaveSecond second) {
        try (Connection connection = pool.getConnection()) {
            connection.set(key, value, second).sync();
        }
    }

    @Override
    public void addAll(List<String> keyList, List<String> valueList, SaveSecond second) {
        if(keyList.size() != valueList.size())
            throw new IllegalArgumentException("keyList.size() != valueList.size()");

        try (Connection connection = pool.getConnection()) {
            for(int i = 0; i < keyList.size(); i++)
                connection.set(keyList.get(i), valueList.get(i), second);

            connection.sync();
        }
    }

    @Override
    public String get(String key, SaveSecond second) {
        try (Connection connection = pool.getConnection()) {
            return connection.get(key, second).sync().getFirst();
        }
    }

    @Override
    public List<String> getList(List<String> keyList, SaveSecond second) {
        try (Connection connection = pool.getConnection()) {
            keyList.forEach(key -> connection.get(key, second));

            return connection.sync();
        }
    }

    @Override
    public void close() {
        pool.close();
        this.pool = null;
    }

}
