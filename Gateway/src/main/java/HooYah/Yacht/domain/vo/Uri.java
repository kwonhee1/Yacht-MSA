package HooYah.Yacht.domain.vo;

import java.net.URI;

public class Uri {

    private String uri; // ex) /user

    public Uri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public static Uri from(URI uri) {
        return new Uri(uri.getRawPath());
    }

    public String toString() {
        return uri;
    }

}
