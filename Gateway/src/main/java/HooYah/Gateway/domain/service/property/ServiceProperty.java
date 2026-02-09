package HooYah.Gateway.domain.service.property;

import HooYah.Gateway.domain.server.Server;
import HooYah.Gateway.domain.service.Service;
import HooYah.Gateway.domain.vo.Port;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProperty {

    @JsonProperty("name")
    private String name;
    @JsonProperty("server")
    private String server;
    @JsonProperty("port")
    private int port;

    public Service toService(List<Server> servers, boolean isRunning) {
        Server serverObj = servers.stream()
            .filter(s -> s.getName().equals(server))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Server " + server + " is not found"));
        
        Port portObj = new Port(port);
        
        if(isRunning)
            return Service.running(name, serverObj, portObj);
        else 
            return Service.sub(name, serverObj, portObj);
    }
}
