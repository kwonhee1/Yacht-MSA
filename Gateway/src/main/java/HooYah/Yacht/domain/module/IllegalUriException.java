package HooYah.Yacht.domain.module;

import HooYah.Yacht.domain.vo.Uri;

public class IllegalUriException extends RuntimeException {
    public IllegalUriException(Uri requestUri) {
        super("Illegal URI: " + requestUri);
    }
}
