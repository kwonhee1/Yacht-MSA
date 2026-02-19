package HooYah.Gateway.provider;

public interface Provider <I, O> {

    Resource<O> provide(I input);

    void release(Resource<O> output);

}
