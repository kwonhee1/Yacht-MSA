package HooYah.Redis;

import HooYah.Redis.pool.InMemoryPool;
import HooYah.Redis.pool.JedisPool;
import HooYah.Redis.pool.Pool;

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

    public static CacheService cacheService(String category, Pool pool) {
        return new CacheServiceImpl(category, pool);
    }

}
