package HooYah.Gateway.domain.service;

import HooYah.Gateway.domain.server.Server;
import HooYah.Gateway.domain.vo.Port;
import HooYah.Gateway.domain.vo.Url;

public class Service {

    private final boolean isRunning;

    private String name;

    private Server server;
    private Port port;

    private ServiceStatus lastStatus;

    private Service(String name, Server server, Port port, boolean isRunning) {
        this.name = name;
        this.server = server;
        this.port = port;
        this.isRunning = isRunning;
    }

    public static Service running(String name, Server server, Port port) {
        return new Service(name, server, port, true);
    }

    public static Service sub(String name, Server server, Port port) {
        return new Service(name, server, port, false);
    }

    public String getName() {
        return name;
    }

    public Url getUrl() {
        return new Url(server.getHost(), port);
    }

    public ServiceStatus getLastStatus() {
        return lastStatus;
    }

    public Port getPort() {
        return port;
    }
}
