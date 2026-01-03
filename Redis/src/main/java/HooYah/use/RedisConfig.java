package HooYah.use;

import HooYah.Redis.pool.ConnectionPool;
import HooYah.Redis.RedisServiceImpl;
import HooYah.Redis.RedisService;
import HooYah.Redis.pool.Pool;

public class RedisConfig {

    private Pool pool = ConnectionPool.generate("host", 6, "password", "username", 3);

    public RedisService userRedisService() {
        return new RedisServiceImpl("REDIS_USER_MODULE_NAME", pool);
    }

}
