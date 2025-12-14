package HooYah.Yacht.domain.module.property;

import HooYah.Yacht.domain.module.Module;
import HooYah.Yacht.domain.service.Service;
import HooYah.Yacht.domain.service.property.ServiceProperty;
import HooYah.Yacht.domain.server.Server;
import HooYah.Yacht.domain.vo.UriMatcher;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleProperty {

    private String matchUri;
    private List<ServiceProperty> services;
    private List<ServiceProperty> subs;

    public Module toModule(List<Server> servers) {
        List<Service> serviceList = services.stream()
            .map(sp -> sp.toService(servers, true))
            .toList();

        List<Service> subServiceList = subs.stream()
            .map(sp -> sp.toService(servers, false))
            .toList();

        return new Module(new UriMatcher(matchUri), serviceList, subServiceList);
    }

}
