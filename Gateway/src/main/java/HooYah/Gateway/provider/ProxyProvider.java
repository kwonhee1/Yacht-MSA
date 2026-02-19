package HooYah.Gateway.provider;

import HooYah.Gateway.loadbalancer.LoadBalancer;
import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.service.Service;
import HooYah.Gateway.loadbalancer.domain.vo.Uri;
import HooYah.Gateway.loadbalancer.domain.vo.Url;
import java.util.ArrayList;
import java.util.List;

public class ProxyProvider implements Provider<String, Url> {

    private final LoadBalancer loadbalancer;
    private final Limiter limiter;

    public ProxyProvider(
            List<Server> servers,
            List<Service> services
    ) {
        this.loadbalancer = new LoadBalancer(servers, services);

        List<Pod> runningPods = new ArrayList<>();
        for(Service service : services)
            runningPods.addAll(service.getPods());

        this.limiter = new Limiter(runningPods);
    }

    @Override
    public Resource<Url> provide(String requestUri) {
        Pod pod = loadbalancer.loadBalance(requestUri);

        if (limiter.tryUpCount(pod)) {
            return new ProxyResource(pod.toUrl(new Uri(requestUri)) ,pod);
        } else {
            throw new TooManyRequest(requestUri);
        }
    }

    @Override
    public void release(Resource<Url> resource) {
        if(!(resource instanceof ProxyResource))
            throw new IllegalArgumentException("try Illegal Resource");

        limiter.downCount(((ProxyResource)resource).pod);
    }

    class ProxyResource extends Resource<Url> {
        private final Pod pod;

        public ProxyResource(Url url, Pod pod) {
            super(url);
            this.pod = pod;
        }

        @Override
        public void release() {
            ProxyProvider.this.release(this);
        }
    }

}
