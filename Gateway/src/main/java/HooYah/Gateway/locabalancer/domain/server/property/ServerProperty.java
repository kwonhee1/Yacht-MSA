package HooYah.Gateway.locabalancer.domain.server.property;

import HooYah.Gateway.locabalancer.domain.server.Server;
import HooYah.Gateway.locabalancer.domain.vo.Host;
import HooYah.Gateway.locabalancer.domain.vo.Protocol;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(value = { "docker" })
public class ServerProperty {

    @JsonProperty("name")
    private String name;
    @JsonProperty("host")
    private String host;
    @JsonProperty("protocol")
    private String protocol;
    @JsonProperty("count")
    private int count;

    public Server toServer() {
        Protocol protocolEnum = Protocol.getProtocol(protocol);
        return new Server(name, protocolEnum, new Host(host), count);
    }

}
