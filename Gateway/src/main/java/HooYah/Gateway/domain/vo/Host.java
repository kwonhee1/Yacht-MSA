package HooYah.Gateway.domain.vo;

public class Host { // http://localhost

    private final Protocol protocol; // http
    private final String host; // localhost

    public Host(Protocol protocol, String host) {
        this.host = host;
        this.protocol = protocol;
    }

    public Host(String hostStr) { // http://ip
        String[] hostStrs =  hostStr.split(":");
        this.protocol = Protocol.getProtocol(hostStrs[0]); // http
        this.host = hostStrs[1].substring(2, hostStrs[1].length());
    }

    public String toString() {
        return protocol.name() + "://" + host;
    }

}
