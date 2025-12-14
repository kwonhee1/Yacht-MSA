package HooYah.Yacht.domain.vo;

import java.net.URI;

public class Api {

    private Url url; // http://localhost:8080
    private Uri uri; // /user/test

    public Api(Url url, Uri uri) {
        this.url = url;
        this.uri = uri;
    }

    public URI toURI() {
        String base = url.toString();
        String path = uri.toString();

        if (base.endsWith("/") && path.startsWith("/"))
            base = base.substring(0, base.length() - 1);

        return URI.create(base + path);
    }

    public Uri getUri() {
        return uri;
    }

    public Url getUrl() {
        return url;
    }

}
