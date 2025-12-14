package HooYah.Yacht.domain.service.property;

import HooYah.Yacht.domain.server.Server;
import HooYah.Yacht.domain.service.Service;
import HooYah.Yacht.domain.vo.Port;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProperty {

    private String name;      // optional
    private String server;    // 서버 이름 (Server의 name과 매칭)
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
