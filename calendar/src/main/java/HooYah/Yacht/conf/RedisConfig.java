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

    @Value("${redis.module.yacht}")
    private String yachtModuleName;
    @Value("${redis.module.user}")
    private String userModuleName;
    @Value("${redis.module.part}")
    private String partModuleName;

    private Pool connectionPool;
    private Pool inMemoryConnectionPool;

    @PostConstruct
    public void init() {
        try {
            connectionPool = Cache.generateRedisPool(host, port, username, password, maxConnectionCount);
        } catch (Exception e) {
            connectionPool = Cache.generateInMemoryPool();
        }
        inMemoryConnectionPool = Cache.generateInMemoryPool();
    }

    @Bean
    public CacheService yachtCacheService() {
        return Cache.cacheService(yachtModuleName, connectionPool);
    }

    @Bean
    public CacheService userCacheService() {
        return Cache.cacheService(userModuleName, connectionPool);
    }

    @Bean
    public CacheService partCacheService() {
        return Cache.cacheService(partModuleName, connectionPool);
    }

    @Bean
    public CacheService inMemoryUserCacheService () {
        // key = {Category}+{UserId}, value = List<Long :: yachtId>
        return Cache.cacheService("UserCache", inMemoryConnectionPool);
    }

}

