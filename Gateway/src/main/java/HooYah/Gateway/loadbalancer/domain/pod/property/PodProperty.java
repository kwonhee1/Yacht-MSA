package HooYah.Gateway.loadbalancer.domain.pod.property;

import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.domain.vo.Port;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PodProperty {

    @JsonProperty("name")
    private String name;
    @JsonProperty("server")
    private String server;
    @JsonProperty("port")
    private int port;

    public Pod toPod(List<Server> servers, boolean isRunning) {
        Server serverObj = servers.stream()
            .filter(s -> s.getName().equals(server))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Server " + server + " is not found"));
        
        Port portObj = new Port(port);
        
        if(isRunning)
            return Pod.running(name, serverObj, portObj);
        else 
            return Pod.sub(name, serverObj, portObj);
    }
}
