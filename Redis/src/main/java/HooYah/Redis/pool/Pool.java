package HooYah.Redis.pool;

import HooYah.Redis.connection.Connection;

public interface Pool {

    Connection getConnection();

}
