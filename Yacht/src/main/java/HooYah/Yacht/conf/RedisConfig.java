package HooYah.Yacht.conf;

import HooYah.Redis.Cache;
import HooYah.Redis.CacheService;
import HooYah.Redis.pool.Pool;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.username}")
    private String username;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.maxConnection}")
    private int maxConnectionCount;

    @Value("${redis.module.user}")
    private String userModuleName;

    private Pool connectionPool;

    @PostConstruct
    public void init() {
        try {
            connectionPool = Cache.generateRedisPool(host, port, username, password, maxConnectionCount);
        } catch (Exception e) {
            connectionPool = Cache.generateInMemoryPool();
        }
    }

    @Bean
    public CacheService userCacheService() {
        return Cache.cacheService(userModuleName, connectionPool);
    }

}

