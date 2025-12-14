package HooYah.Yacht.domain.server.property;

import HooYah.Yacht.domain.server.Server;
import HooYah.Yacht.domain.vo.Host;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerProperty {

    private String name;
    private String host; // protocol, ip
    private int count;

    public Server toServer() {
        return new Server(name, new Host(host), count);
    }

}
