package HooYah.Gateway.domain.vo;

// URL = <protocol>://<host>:<port>/<uri>
public class Url {

    private final Protocol protocol;
    private final Host host;
    private final Port port;
    private final Uri uri;

    public Url(Protocol protocol, Host host, Port port, Uri uri) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.uri = uri;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Host getHost() {
        return host;
    }

    public Port getPort() {
        return port;
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return protocol.name() + "://" + host + ":" + port + uri.getUri();
    }

}
