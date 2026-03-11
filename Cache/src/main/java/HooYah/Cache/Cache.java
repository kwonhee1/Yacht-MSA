package HooYah.Cache;

import HooYah.Cache.pool.InMemoryPool;
import HooYah.Cache.pool.JedisPool;
import HooYah.Cache.pool.Pool;
import HooYah.Cache.template.CacheTemplate;
import HooYah.Cache.template.Template;
import java.util.Map;

/*
    Cache Library Main Class
 */
public class Cache {

    public static Pool generateRedisPool(String host, int port, String username, String password, int maxConnection) {
        return new JedisPool(host, port, username, password, maxConnection);
    }

    public static Pool generateInMemoryPool() {
        return new InMemoryPool();
    }

    public static <T> Template cacheTemplate(Pool pool) {
        return new CacheTemplate(pool);
    }

    public static <T> CacheService<T> cacheService(String category, Pool pool, Class<T> type) {
        return new CacheServiceImpl<>(category, cacheTemplate(pool), type);
    }

    public static CacheService cacheService(String category, Pool pool) {
        return new CacheServiceImpl(category, cacheTemplate(pool), Map.class);
    }

}
