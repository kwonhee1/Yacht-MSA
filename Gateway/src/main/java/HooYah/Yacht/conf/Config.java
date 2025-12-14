package HooYah.Yacht.conf;

import HooYah.Yacht.domain.module.Modules;
import HooYah.Yacht.domain.module.property.ModuleProperty;
import HooYah.Yacht.domain.server.Server;
import HooYah.Yacht.domain.server.property.ServerProperty;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "custom")
@Getter
@Setter
public class Config {

    private List<ServerProperty> servers;
    private List<ModuleProperty> modules;

    private List<Server> serverList;
    private Modules modulesObj;

    @PostConstruct
    public void init() {
        serverList = servers.stream()
            .map(ServerProperty::toServer)
            .toList();

        modulesObj = new Modules(
            modules.stream()
                .map(mp -> mp.toModule(serverList))
                .toList()
        );
    }

    @Bean
    public Modules modules() {
        return modulesObj;
    }

    @Bean
    public List<Server> servers() {
        return serverList;
    }

}
