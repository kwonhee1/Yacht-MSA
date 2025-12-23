package HooYah.test;

import HooYah.Redis.ConnectionPool;
import HooYah.Redis.RedisServiceImpl;
import HooYah.Redis.RedisService;

public class RedisConfig {

    private ConnectionPool connectionPool = ConnectionPool.generate("host", 6, "password", "username", 3);

    public RedisService userRedisService() {
        return new RedisServiceImpl("REDIS_USER_MODULE_NAME", connectionPool);
    }

}
