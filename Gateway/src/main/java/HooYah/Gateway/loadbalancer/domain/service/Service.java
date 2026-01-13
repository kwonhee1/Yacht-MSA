package HooYah.Gateway.loadbalancer.domain.service;

import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.vo.Port;

public class Service {

    private final boolean isRunning;

    private String name;

    private final Server server;
    private final Port port;

    private ServiceStatus lastStatus;

    private Service(String name, Server server, Port port, boolean isRunning) {
        this.name = name;
        this.server = server; 
        this.port = port;
        this.isRunning = isRunning;

        if(isRunning)
            server.addService(this);
    }

    public static Service running(String name, Server server, Port port) {
        return new Service(name, server, port, true);
    }

    public static Service sub(String name, Server server, Port port) {
        return new Service(name, server, port, false);
    }

    // getter

    public String getName() {
        return name;
    }

    public Port getPort() {
        return port;
    }

    public Server getServer() {
        return server;
    }

    public ServiceStatus getLastStatus() {
        return lastStatus;
    }

    @Override
    public String toString() {
        return "Service{" +
                "isRunning=" + isRunning +
                ", name='" + name + '\'' +
                ", server=" + server +
                ", port=" + port +
                ", lastStatus=" + lastStatus +
                '}';
    }
}
