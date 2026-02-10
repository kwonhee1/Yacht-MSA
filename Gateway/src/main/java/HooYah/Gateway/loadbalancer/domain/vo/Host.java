package HooYah.Gateway.loadbalancer.domain.vo;

public class Host {

    private final String host; // localhost

    public Host(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    @Override
    public String toString() {
        return host;
    }

}
