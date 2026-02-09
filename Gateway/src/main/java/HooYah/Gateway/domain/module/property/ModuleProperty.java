package HooYah.Gateway.domain.module.property;

import HooYah.Gateway.domain.module.Module;
import HooYah.Gateway.domain.service.Service;
import HooYah.Gateway.domain.service.property.ServiceProperty;
import HooYah.Gateway.domain.server.Server;
import HooYah.Gateway.domain.vo.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleProperty {

    @JsonProperty("match-uri")
    private List<String> matchUriList;
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

        List<Uri> uriList = matchUriList.stream().map(Uri::new).toList();
        return new Module(uriList, serviceList, subServiceList);
    }

}
