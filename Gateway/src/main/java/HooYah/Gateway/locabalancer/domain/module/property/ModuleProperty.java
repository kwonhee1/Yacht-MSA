package HooYah.Gateway.locabalancer.domain.module.property;

import HooYah.Gateway.locabalancer.domain.module.Module;
import HooYah.Gateway.locabalancer.domain.service.Service;
import HooYah.Gateway.locabalancer.domain.service.property.ServiceProperty;
import HooYah.Gateway.locabalancer.domain.server.Server;
import HooYah.Gateway.locabalancer.domain.vo.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleProperty {

    @JsonProperty("match-uri")
    private String matchUri;
    @JsonProperty("services")
    private List<ServiceProperty> services;
    @JsonProperty("subs")
    private List<ServiceProperty> subs;

    public Module toModule(List<Server> servers) {
        List<Service> serviceList = services.stream()
            .map(sp -> sp.toService(servers, true))
            .toList();

        List<Service> subServiceList = subs.stream()
            .map(sp -> sp.toService(servers, false))
            .toList();

        Uri uri = new Uri(matchUri);
        return new Module(uri, serviceList, subServiceList);
    }

}
