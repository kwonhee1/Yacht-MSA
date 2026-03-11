package HooYah.Cache.pool;

import HooYah.Cache.ResourceClosedException;

public class PoolClosedException extends ResourceClosedException {
    public PoolClosedException(Pool pool) {
        super("pool " + pool.toString() + " closed");
    }
}
