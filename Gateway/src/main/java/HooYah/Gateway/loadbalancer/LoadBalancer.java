package HooYah.Gateway.loadbalancer;

import HooYah.Gateway.loadbalancer.checker.CheckerService;
import HooYah.Gateway.loadbalancer.checker.status.ServiceStatus;
import HooYah.Gateway.loadbalancer.domain.module.IllegalUriException;
import HooYah.Gateway.loadbalancer.domain.module.Module;
import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.service.Service;
import HooYah.Gateway.loadbalancer.domain.vo.Uri;
import HooYah.Gateway.loadbalancer.domain.vo.Url;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadBalancer {

    private final List<Server> servers;
    private final List<Module> modules;

    private final CheckerService statusChecker;

    private static final Logger logger = LoggerFactory.getLogger("Loadbalancer");

    public LoadBalancer(List<Server> servers, List<Module> modules) {
        this.servers = servers;
        this.modules = modules;

        List<Service> runningServiceList = new ArrayList<>();
        modules.stream().forEach(module-> runningServiceList.addAll(module.getServices()));
        this.statusChecker = new CheckerService(runningServiceList);
    }

    public Url loadBalance(String uri) {
        Uri requestUri = new Uri(uri);

        Module matchedModule = findMatchedModule(requestUri);
        Service matchedService = loadBalancing(matchedModule);
        Server matchedServer = matchedService.getServer();

        return new Url(matchedServer.getProtocol(), matchedServer.getHost(), matchedService.getPort(), requestUri);
    }

    private Module findMatchedModule(Uri requestUri) {
        for(Module module : modules){
            if(module.matches(requestUri))
                return module;
        }
        throw new IllegalUriException(requestUri);
    }

    private Service loadBalancing(Module module) {
        List<Service> serviceList = module.getServices();
        List<ServiceStatus> statusList = serviceList.stream().map(statusChecker::getStatus).toList();

        int bestServiceIndex = 0;
        for(int i = 1; i < serviceList.size(); i++) {
            ServiceStatus bestStatus = statusList.get(bestServiceIndex);
            if(bestStatus.compareTo(statusList.get(i)) > 0)
                bestServiceIndex = i;
        }

        if(statusList.get(bestServiceIndex) == ServiceStatus.DEAD) {
            logger.error(" all services are dead!! " + module.toString()); // todo : 다음 로직 고민하기
        }

        return serviceList.get(bestServiceIndex);
    }

}
