package HooYah.Gateway.loadbalancer.domain.pod;

import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.vo.Port;
import HooYah.Gateway.loadbalancer.domain.vo.Uri;
import HooYah.Gateway.loadbalancer.domain.vo.Url;

public class Pod {

    private final boolean isRunning;

    private String name;

    private final Server server;
    private final Port port;

    private Pod(String name, Server server, Port port, boolean isRunning) {
        this.name = name;
        this.server = server; 
        this.port = port;
        this.isRunning = isRunning;

        if(isRunning)
            server.addPod(this);
    }

    public static Pod running(String name, Server server, Port port) {
        return new Pod(name, server, port, true);
    }

    public static Pod sub(String name, Server server, Port port) {
        return new Pod(name, server, port, false);
    }

    public Url toUrl(Uri requestUri) {
        Server matchedServer = getServer();

        return new Url(matchedServer.getProtocol(), matchedServer.getHost(), port, requestUri);
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
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public String toString() {
        return "Pod{" +
                "isRunning=" + isRunning +
                ", name='" + name + '\'' +
                ", server=" + server +
                ", port=" + port +
                '}';
    }
}
