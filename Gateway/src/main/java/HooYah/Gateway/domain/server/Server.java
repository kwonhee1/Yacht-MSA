package HooYah.Gateway.domain.server;

import HooYah.Gateway.domain.service.Service;
import HooYah.Gateway.domain.vo.Host;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private String name;
    private Host host;

    private int maxCount;

    private List<Service> services = new ArrayList<>();

    public Server(String name, Host host, int maxCount) {
        this.name = name;
        this.host = host;
        this.maxCount = maxCount;
    }

    public void addService(Service service) {
        services.add(service);
    }

    public void deleteService(Service service) {
        services.remove(service);
    }

    public Host getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

}
