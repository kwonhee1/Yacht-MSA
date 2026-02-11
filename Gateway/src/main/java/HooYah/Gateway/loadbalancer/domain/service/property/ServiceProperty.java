package HooYah.Gateway.loadbalancer.domain.service.property;

import HooYah.Gateway.loadbalancer.domain.service.Service;
import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.domain.pod.property.PodProperty;
import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.vo.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProperty {

    @JsonProperty("match-uri")
    private List<String> matchUriList;
    @JsonProperty("pods")
    private List<PodProperty> pods;
    @JsonProperty("subs")
    private List<PodProperty> subPods;

    public Service toService(List<Server> servers) {
        List<Pod> podList = pods.stream()
            .map(sp -> sp.toPod(servers, true))
            .toList();

        List<Pod> subPodList = subPods.stream()
            .map(sp -> sp.toPod(servers, false))
            .toList();

        List<Uri> uriList = matchUriList.stream().map(Uri::new).toList();
        return new Service(uriList, podList, subPodList);
    }

}
