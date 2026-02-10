package HooYah.Gateway.loadbalancer.domain;

import HooYah.Gateway.config.ConfigFile;
import HooYah.Gateway.loadbalancer.domain.module.Module;
import HooYah.Gateway.loadbalancer.domain.module.property.ModuleProperty;
import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.server.property.ServerProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerServiceContext {

    private static final ServerServiceContext INSTANCE = ServerServiceContext.readData();

    private final List<Server> servers;
    private final List<Module> modules;

    private Logger logger = LoggerFactory.getLogger(ServerServiceContext.class);

    private static ServerServiceContext readData() {
        return new ServerServiceContext();
    }
    private ServerServiceContext() {
        servers = initServers();
        modules = initModules();
    }

    public static List<Server> getServers() {
        return INSTANCE.servers;
    }

    public static List<Module> getModules() {
        return INSTANCE.modules;
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

    private List<Module> initModules() {
        List<ModuleProperty> moduleProperties = ((ServersProperty)ConfigFile.SERVER_YML.getValue(ServersProperty.class)).getModuleProperties();
        List<Module> moduleList = moduleProperties.stream()
                .map(f->f.toModule(servers))
                .toList();

        for(Module module : moduleList) {
            logger.info(module.toString());
        }

        return moduleList;
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
