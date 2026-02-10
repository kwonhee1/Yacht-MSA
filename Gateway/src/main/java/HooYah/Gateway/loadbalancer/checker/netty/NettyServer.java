package HooYah.Gateway.loadbalancer.checker.netty;

import HooYah.Gateway.loadbalancer.checker.CheckerService.StatusType;
import HooYah.Gateway.loadbalancer.checker.status.ServiceStatus;
import HooYah.Gateway.loadbalancer.domain.service.Service;
import HooYah.Gateway.loadbalancer.domain.vo.Host;
import HooYah.Gateway.loadbalancer.domain.vo.Port;
import HooYah.Gateway.loadbalancer.checker.CheckerService;
import HooYah.Gateway.loadbalancer.checker.netty.handler.DockerReceiverHandler;
import HooYah.Gateway.loadbalancer.checker.netty.handler.TestApiReceiverHandler;

import HooYah.Gateway.loadbalancer.domain.vo.Protocol;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {

    private final CheckerService checkerService;

    private final EventLoopGroup eventGroup = new NioEventLoopGroup();

    private final Map<Service, Bootstrap> testApiClients;
    private final Map<Service, Bootstrap> dockerClients;

    private final Logger logger = LoggerFactory.getLogger("LoadBalancer(NettyServer)");

    public NettyServer(
            List<Service> serviceList,
            CheckerService checkerService
    ) {
        this.checkerService = checkerService;

        // init clients
        Map<Service, String> containerIdMap = new DockerContainerReader().getDockerContainerIdMap(serviceList);

        dockerClients = new HashMap<>();
        testApiClients = new HashMap<>(serviceList.size());

        for (Service service : serviceList) {
            testApiClients.put(service, makeClient(
                    service.getServer().getHost(),
                    service.getPort(),
                    new TestApiReceiverHandler(service, checkerService)
            ));
            dockerClients.put(service, makeClient(
                    new Host("yacht.r-e.kr"), new Port(2375),
                    new DockerReceiverHandler(service, containerIdMap.get(service), checkerService)
            ));
        }
    }

    public void start() {
        sendClients();
    }

    private Bootstrap makeClient(Host host, Port port, ChannelHandler handler) {
        SocketAddress address = InetSocketAddress.createUnresolved(host.getHost(), port.getPort());

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(address)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpObjectAggregator(1048576)); // 최대 1MB
                        p.addLast(handler);
                    }
                })
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000) // 30초
                ;

        return bootstrap;
    }

    private void sendClients() {
        List<Channel> sendClients = new ArrayList<>(dockerClients.size() + testApiClients.size());
        for(Service service : dockerClients.keySet()) {
            ChannelFuture channelFuture = dockerClients.get(service).connect();

            channelFuture.addListener(future -> {
                if(channelFuture.isSuccess()) {
                    sendClients.add(channelFuture.channel());
                } else {
                    logger.info(String.format("Docker status connection fail server %s service %s", service.getServer().getName(), service.getName()));
                    checkerService.addStatus(service, ServiceStatus.DEAD, StatusType.Docker);
                    channelFuture.channel().close();
                }
            });
        }

        for(Service service : testApiClients.keySet()) {
            ChannelFuture channelFuture = testApiClients.get(service).connect();

            channelFuture.addListener(future -> {
                if (channelFuture.isSuccess()) {
                    channelFuture.channel().attr(NettyServerContext.sendTimeAttr).set(LocalDateTime.now());
                    sendClients.add(channelFuture.channel());
                } else {
                    logger.info(String.format("TestApi status connection fail server %s service %s", service.getServer().getName(), service.getName()));
                    checkerService.addStatus(service, ServiceStatus.DEAD, StatusType.TestApi);
                    channelFuture.channel().close();
                }
            });
        }

        eventGroup.schedule(
                ()->{
                    logger.info("NettyServer :: request time out! close all requests " + LocalDateTime.now());
                    for(Channel sendClient : sendClients) {
                        sendClient.close();
                    }
                },
                30,
                TimeUnit.SECONDS
        );

        eventGroup.schedule(
                ()->sendClients(),
                60,
                TimeUnit.SECONDS
        );
    }

    class DockerContainerReader {

        static ObjectMapper objectMapper = NettyServerContext.getObjectMapper();

        public Map<Service, String> getDockerContainerIdMap(List<Service> serviceList) {
            List<Map<String, Object>> dockerPsResponse = getContainerIdMap();
            Map<Service, String> containerIdMap = toDockerContainerId(serviceList, dockerPsResponse);

            return containerIdMap;
        }

        private List<Map<String, Object>> getContainerIdMap() {
            String response;
            try {
                // send docker ps api
                HttpRequest httpRequest =
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("https://example.com"))
                                .build();

                HttpResponse<String> httpResponse = HttpClient.newHttpClient()
                        .send(httpRequest, HttpResponse.BodyHandlers.ofString());

                response = httpResponse.body();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
            } catch (IOException e) {
                return List.of();
            }
        }

        private Map<Service, String> toDockerContainerId(
                List<Service> serviceList,
                List<Map<String, Object>> dockerPsResponse
        ) {
            Map<Service, String> containerIdMap = new HashMap<>();
            for(Service service : serviceList)
                containerIdMap.put(service, getContainerId(service, dockerPsResponse));

            return containerIdMap;
        }

        private String getContainerId(
                Service service,
                List<Map<String, Object>> dockerPsResponse
        ) {
            for(Map<String, Object> container : dockerPsResponse) {
                String containerName = ((List<String>)container.get("Names")).get(0);

                if(containerName.equals("/" + service.getName()))
                    return (String) container.get("Id");
            }

            return null; // 추후 DockerReceiverHandler에서 fail하게 됨
        }
    }

}
