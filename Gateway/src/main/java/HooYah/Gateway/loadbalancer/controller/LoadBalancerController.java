package HooYah.Gateway.loadbalancer.controller;

import HooYah.Gateway.loadbalancer.domain.module.Module;
import HooYah.Gateway.loadbalancer.domain.module.Modules;
import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.service.Service;
import HooYah.Gateway.loadbalancer.domain.vo.Uri;
import HooYah.Gateway.loadbalancer.domain.vo.Url;
import java.util.List;

public class LoadBalancerController {

    private final List<Server> servers;
    private final Modules modules;

    public LoadBalancerController(List<Server> servers, Modules modules) {
        this.servers = servers;
        this.modules = modules;
    }

    public Url loadBalance(String uri) {
        Uri requestUri = new Uri(uri);

        Module matchedModule = modules.matching(requestUri);
        Service matchedService = matchedModule.matching();
        Server matchedServer = matchedService.getServer();

        return new Url(matchedServer.getProtocol(), matchedServer.getHost(), matchedService.getPort(), requestUri);
    }

}
