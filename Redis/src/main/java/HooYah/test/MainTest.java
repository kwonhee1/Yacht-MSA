package HooYah.test;

import HooYah.Redis.RedisService;
import java.util.Optional;

public class MainTest {

    public static void main(String[] args) {
        RedisConfig redisConfig = new RedisConfig();
        RedisService redisService = redisConfig.userRedisService();

        redisService.getOrSelect(1L, Data.class, ()-> Optional.of(new Data()));
    }

    static class Data {
        String data = "Data";
    }
}
