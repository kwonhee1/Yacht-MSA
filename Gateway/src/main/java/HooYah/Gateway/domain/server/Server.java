package HooYah.Gateway.domain.server;

import HooYah.Gateway.domain.service.Service;
import HooYah.Gateway.domain.vo.Host;
import HooYah.Gateway.domain.vo.Protocol;
import java.util.ArrayList;
import java.util.List;

// server: protocol, host
public class Server {

    private String name;
    private final Protocol protocol;
    private final Host host;

    private int maxCount;

    private List<Service> services = new ArrayList<>();

    public Server(String name, Protocol protocol, Host host, int maxCount) {
        this.name = name;
        this.protocol = protocol;
        this.host = host;
        this.maxCount = maxCount;
    }

    public void addService(Service service) {
        services.add(service);
    }

    public void deleteService(Service service) {
        services.remove(service);
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Host getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Server{" +
                "name='" + name + '\'' +
                ", protocol=" + protocol +
                ", host=" + host +
                ", maxCount=" + maxCount +
                // ", services=" + services +
                '}';
    }
}
