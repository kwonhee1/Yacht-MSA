package HooYah.Gateway.loadbalancer;

import HooYah.Gateway.domain.module.Module;
import HooYah.Gateway.domain.server.Server;
import HooYah.Gateway.domain.service.Service;
import HooYah.Gateway.domain.vo.Host;
import HooYah.Gateway.domain.vo.Port;
import HooYah.Gateway.domain.vo.Protocol;
import HooYah.Gateway.domain.vo.Uri;
import HooYah.Gateway.domain.vo.Url;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoadBalancerTest {

    private Server server;
    private Modules modules;

    private LoadBalancer loadBalancer;

    @BeforeEach
    void setUp() {
        server = new Server("server1", Protocol.http, new Host("localhost"), 10);

        Module partModule = new Module(
                List.of(new Uri("/part/"), new Uri("/repair/")),
                List.of(Service.running("part", server, new Port(8081))),
                new ArrayList<>()
        );

        Module calendarModule = new Module(
                List.of(new Uri("/calendar/"), new Uri("/alarm/")),
                List.of(Service.running("calendar", server, new Port(8080))),
                new ArrayList<>()
        );

        // The Modules collection
        modules = new Modules(List.of(partModule, calendarModule));

        loadBalancer = new LoadBalancer(List.of(server), modules);
    }

    // 다른 방법의 test 없s나?
    @Test
    public void testUserUriMatching() {
        Url proxy1 = loadBalancer.loadBalance("/repair/test?test=test");

        Assertions.assertEquals(proxy1.getPort().getPort(), 8081);
        Assertions.assertEquals(proxy1.getUri().getUri(), "/repair/test?test=test");

        Url proxy2 = loadBalancer.loadBalance("/calendar?test=test");

        Assertions.assertEquals(proxy2.getPort().getPort(), 8080);
        Assertions.assertEquals(proxy2.getUri().getUri(), "/calendar?test=test");

        Assertions.assertThrows(RuntimeException.class, () -> loadBalancer.loadBalance("/nothing"));
    }

}
