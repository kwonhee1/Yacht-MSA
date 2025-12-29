package HooYah.Redis.connection;

public interface Connection extends AutoCloseable {
    Pipeline pipeline();

    void set(String key, String value, SaveSecond second);
    String get(String key, SaveSecond second);

    @Override
    void close();

}
