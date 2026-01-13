package HooYah.Gateway.loadbalancer.domain.module;

import HooYah.Gateway.loadbalancer.domain.vo.Uri;

public class IllegalUriException extends RuntimeException {
    public IllegalUriException(Uri requestUri) {
        super("Illegal URI: " + requestUri);
    }
}
