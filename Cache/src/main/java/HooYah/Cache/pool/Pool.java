package HooYah.Cache.pool;

import HooYah.Cache.connection.Connection;

public interface Pool {

    Connection getConnection();

}
