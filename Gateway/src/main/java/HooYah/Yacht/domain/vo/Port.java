package HooYah.Yacht.domain.vo;

public class Port {

    private final int port;

    public Port(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return String.valueOf(port);
    }

}
