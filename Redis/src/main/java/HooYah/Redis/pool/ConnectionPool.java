package HooYah.Redis.pool;

public class ConnectionPool {

    private static Pool instance;

    private ConnectionPool() {}

    public static Pool generate(String host, int port, String username, String password, int maxConnection) {
        try {
            instance = new JedisPool(host, port, username, password, maxConnection);
        } catch (RuntimeException e) {
            instance = new InMemoryPool();
        }

        return instance;
    }

    public static Pool getPool() {
        if(instance == null)
            throw new RuntimeException("Instance Not Exist, generate before");
        return instance;
    }

}
