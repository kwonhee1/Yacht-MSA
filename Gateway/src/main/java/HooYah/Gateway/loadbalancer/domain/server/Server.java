package HooYah.Gateway.loadbalancer.domain.server;

import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.domain.vo.Host;
import HooYah.Gateway.loadbalancer.domain.vo.Protocol;
import java.util.ArrayList;
import java.util.List;

// server: protocol, host
public class Server {

    private String name;
    private final Protocol protocol;
    private final Host host;

    private int maxCount;

    private List<Pod> pods = new ArrayList<>();

    public Server(String name, Protocol protocol, Host host, int maxCount) {
        this.name = name;
        this.protocol = protocol;
        this.host = host;
        this.maxCount = maxCount;
    }

    public void addPod(Pod pod) {
        pods.add(pod);
    }

    public void deletePod(Pod pod) {
        pods.remove(pod);
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
                // ", pods=" + pods +
                '}';
    }
}
