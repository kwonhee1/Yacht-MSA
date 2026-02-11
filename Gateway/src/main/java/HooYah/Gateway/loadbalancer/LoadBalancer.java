package HooYah.Gateway.loadbalancer;

import HooYah.Gateway.loadbalancer.checker.CheckerService;
import HooYah.Gateway.loadbalancer.checker.status.ServiceStatus;
import HooYah.Gateway.loadbalancer.domain.service.IllegalUriException;
import HooYah.Gateway.loadbalancer.domain.service.Service;
import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.domain.vo.Uri;
import HooYah.Gateway.loadbalancer.domain.vo.Url;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadBalancer {

    private final List<Server> servers;
    private final List<Service> services;

    private final CheckerService statusChecker;

    private static final Logger logger = LoggerFactory.getLogger("Loadbalancer");

    public LoadBalancer(List<Server> servers, List<Service> services) {
        this.servers = servers;
        this.services = services;

        List<Pod> runningPodList = new ArrayList<>();
        services.stream().forEach(service -> runningPodList.addAll(service.getPods()));
        this.statusChecker = new CheckerService(runningPodList);
    }

    public Url loadBalance(String uri) {
        Uri requestUri = new Uri(uri);

        Service matchedService = findMatchedService(requestUri);
        Pod matchedPod = loadBalancing(matchedService);
        Server matchedServer = matchedPod.getServer();

        return new Url(matchedServer.getProtocol(), matchedServer.getHost(), matchedPod.getPort(), requestUri);
    }

    private Service findMatchedService(Uri requestUri) {
        for(Service service : services){
            if(service.matches(requestUri))
                return service;
        }
        throw new IllegalUriException(requestUri);
    }

    private Pod loadBalancing(Service service) {
        List<Pod> podList = service.getPods();
        List<ServiceStatus> statusList = podList.stream().map(statusChecker::getStatus).toList();

        int bestServiceIndex = 0;
        for(int i = 1; i < podList.size(); i++) {
            ServiceStatus bestStatus = statusList.get(bestServiceIndex);
            if(bestStatus.compareTo(statusList.get(i)) > 0)
                bestServiceIndex = i;
        }

        if(statusList.get(bestServiceIndex) == ServiceStatus.DEAD) {
            logger.error(" all services are dead!! " + service.toString()); // todo : 다음 로직 고민하기
        }

        return podList.get(bestServiceIndex);
    }

}
