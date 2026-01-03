package HooYah.Yacht.conf;

import HooYah.Redis.RedisService;
import HooYah.Redis.RedisServiceImpl;
import HooYah.Redis.pool.ConnectionPool;
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

    private Pool connectionPool;

    @PostConstruct
    public void init() {
        connectionPool = ConnectionPool.generate(host, port, password, username, maxConnectionCount);
    }

    @Bean
    public RedisService yachtRedisService() {
        return new RedisServiceImpl(yachtModuleName, connectionPool);
    }

    @Bean
    public RedisService userRedisService() {
        return new RedisServiceImpl(userModuleName, connectionPool);
    }

}

