package HooYah.Cache.connection;

import HooYah.Cache.ResourceClosedException;

public class ConnectionClosedException extends ResourceClosedException {

    public ConnectionClosedException(Connection c) {
        super("connection " + c.toString() + " already closed" );
    }

}
