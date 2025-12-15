package HooYah.Gateway.domain.module;

import HooYah.Gateway.domain.vo.Uri;

public class IllegalUriException extends RuntimeException {
    public IllegalUriException(Uri requestUri) {
        super("Illegal URI: " + requestUri);
    }
}
