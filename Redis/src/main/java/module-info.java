module YachtUser.main {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires redis.clients.jedis;
    requires org.apache.commons.pool2;
    requires org.slf4j; // used at jedis, pool2 :: implementation("org.slf4j:slf4j-simple:2.0.17")

    exports HooYah.Redis;

    // connection
    exports HooYah.Redis.connection;
    exports HooYah.Redis.connection.jedis;
    exports HooYah.Redis.connection.inmemory;

    //pool
    exports HooYah.Redis.pool;

    // template
    exports HooYah.Redis.template;

}