package HooYah.Yacht.domain.vo;

public class Url {

    private Host host;
    private Port port;

    public Url(Host host, Port port) {
        this.host = host;
        this.port = port;
    }

    public Host getHost() {
        return host;
    }
    public Port getPort() {
        return port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

}
