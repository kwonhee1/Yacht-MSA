package HooYah.Gateway.loadbalancer.domain.service;

import HooYah.Gateway.loadbalancer.domain.vo.Uri;

public class IllegalUriException extends RuntimeException {
    public IllegalUriException(Uri requestUri) {
        super("Illegal URI: " + requestUri);
    }
}
