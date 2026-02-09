package HooYah.Gateway.loadbalancer;

import HooYah.Gateway.domain.module.IllegalUriException;
import HooYah.Gateway.domain.module.Module;
import HooYah.Gateway.domain.server.Server;
import HooYah.Gateway.domain.service.Service;
import HooYah.Gateway.domain.vo.Uri;
import HooYah.Gateway.domain.vo.Url;
import java.util.List;

public class LoadBalancer {

    private final List<Server> servers;
    private final List<Module> modules;

    // private final CheckerController checker;

    public LoadBalancer(List<Server> servers, List<Module> modules) {
        this.servers = servers;
        this.modules = modules;
        // this.checker = new CheckerController();
    }

    public Url loadBalance(String uri) {
        Uri requestUri = new Uri(uri);

        Module matchedModule = findMatchedModule(requestUri);
        Service matchedService = loadBalancing(matchedModule);
        Server matchedServer = matchedService.getServer();

        return new Url(matchedServer.getProtocol(), matchedServer.getHost(), matchedService.getPort(), requestUri);
    }

    private Module findMatchedModule(Uri requestUri) {
        Module matchedModule;
        for(Module module : modules){
            return module;
        }
        throw new IllegalUriException(requestUri);
    }

    private Service loadBalancing(Module module) {
        List<Service> serviceList = module.getServices();

        // Service별 status 를 get 하고
        // status 기준 고려해서 service를 선정함

        return serviceList.get(0); // 일단은 첫번쨰 Service를 반환함
    }

}
