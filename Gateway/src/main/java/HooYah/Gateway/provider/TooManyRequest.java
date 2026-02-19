package HooYah.Gateway.provider;

public class TooManyRequest extends RuntimeException {
    public TooManyRequest(String requestUri) {
        super("request to " + requestUri + " too many times");
    }
}
