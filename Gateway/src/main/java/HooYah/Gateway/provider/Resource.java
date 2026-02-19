package HooYah.Gateway.provider;

public abstract class Resource<T> {
    private final T data;

    public Resource(T data) {
        this.data = data;
    }

    public T get() {
        return data;
    }

    public abstract void release();
}
