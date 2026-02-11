package HooYah.Gateway.loadbalancer.domain;

import HooYah.Gateway.config.ConfigFile;
import HooYah.Gateway.loadbalancer.domain.service.Service;
import HooYah.Gateway.loadbalancer.domain.service.property.ServiceProperty;
import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.server.property.ServerProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerServiceContext {

    private static final ServerServiceContext INSTANCE = ServerServiceContext.readData();

    private final List<Server> servers;
    private final List<Service> services;

    private Logger logger = LoggerFactory.getLogger(ServerServiceContext.class);

    private static ServerServiceContext readData() {
        return new ServerServiceContext();
    }
    private ServerServiceContext() {
        servers = initServers();
        services = initServices();
    }

    public static List<Server> getServers() {
        return INSTANCE.servers;
    }

    public static List<Service> getServices() {
        return INSTANCE.services;
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

    private List<Service> initServices() {
        List<ServiceProperty> serviceProperties = ((ServersProperty)ConfigFile.SERVER_YML.getValue(ServersProperty.class)).getServiceProperties();
        List<Service> serviceList = serviceProperties.stream()
                .map(f->f.toService(servers))
                .toList();

        for(Service service : serviceList) {
            logger.info(service.toString());
        }

        return serviceList;
    }

    static class ServersProperty {
        @JsonProperty("servers")
        private List<ServerProperty> servers;
        @JsonProperty("services")
        private List<ServiceProperty> services;

        public ServersProperty(List<ServerProperty> servers, List<ServiceProperty> services) {
            this.servers = servers;
            this.services = services;
        }

        public ServersProperty() {
        }

        public List<ServerProperty> getServerProperties() {
            return servers;
        }

        public List<ServiceProperty> getServiceProperties() {
            return services;
        }
    }

}
