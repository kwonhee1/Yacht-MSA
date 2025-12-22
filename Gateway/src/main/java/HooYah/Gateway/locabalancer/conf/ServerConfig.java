package HooYah.Gateway.locabalancer.conf;

import HooYah.Gateway.config.ConfigFile;
import HooYah.Gateway.locabalancer.domain.module.Modules;
import HooYah.Gateway.locabalancer.domain.module.Module;
import HooYah.Gateway.locabalancer.domain.module.property.ModuleProperty;
import HooYah.Gateway.locabalancer.domain.server.Server;
import HooYah.Gateway.locabalancer.domain.server.property.ServerProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConfig {

    private final List<Server> servers;
    private final Modules modules;

    private Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    public ServerConfig() {
        servers = initServers();
        modules = initModules();
    }

    public List<Server> getServers() {
        return servers;
    }

    public Modules getModules() {
        return modules;
    }

    private List<Server> initServers() {
        //List<ServerProperty> serverProperties = yamlReader.getValueList("servers", ServerProperty.class);
        List<ServerProperty> serverProperties = ((ServersProperty)ConfigFile.SERVER_YML.getValue(ServersProperty.class)).getServerProperties();
        List<Server> serverList = serverProperties.stream()
                .map(ServerProperty::toServer)
                .toList();

        for(Server server : serverList) {
            logger.info(server.toString());
        }

        return serverList;
    }

    private Modules initModules() {
        List<ModuleProperty> moduleProperties = ((ServersProperty)ConfigFile.SERVER_YML.getValue(ServersProperty.class)).getModuleProperties();
        List<Module> moduleList = moduleProperties.stream()
                .map(f->f.toModule(servers))
                .toList();

        for(Module module : moduleList) {
            logger.info(module.toString());
        }

        return new Modules(moduleList);
    }

    static class ServersProperty {
        @JsonProperty("servers")
        private List<ServerProperty> servers;
        @JsonProperty("modules")
        private List<ModuleProperty> modules;

        public ServersProperty(List<ServerProperty> servers, List<ModuleProperty> modules) {
            this.servers = servers;
            this.modules = modules;
        }

        public ServersProperty() {
        }

        public List<ServerProperty> getServerProperties() {
            return servers;
        }

        public List<ModuleProperty> getModuleProperties() {
            return modules;
        }
    }

}
