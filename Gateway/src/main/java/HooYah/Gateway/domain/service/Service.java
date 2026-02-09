package HooYah.Gateway.domain.service;

import HooYah.Gateway.domain.server.Server;
import HooYah.Gateway.domain.vo.Port;

public class Service {

    private final boolean isRunning;

    private String name;

    private final Server server;
    private final Port port;

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

    @Override
    public String toString() {
        return "Service{" +
                "isRunning=" + isRunning +
                ", name='" + name + '\'' +
                ", server=" + server +
                ", port=" + port +
                '}';
    }
}
