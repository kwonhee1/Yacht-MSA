package HooYah.Gateway.loadbalancer.checker.netty;

import HooYah.Gateway.loadbalancer.checker.CheckerService.StatusType;
import HooYah.Gateway.loadbalancer.checker.status.ServiceStatus;
import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.domain.vo.Host;
import HooYah.Gateway.loadbalancer.domain.vo.Port;
import HooYah.Gateway.loadbalancer.checker.CheckerService;
import HooYah.Gateway.loadbalancer.checker.netty.handler.DockerReceiverHandler;
import HooYah.Gateway.loadbalancer.checker.netty.handler.TestApiReceiverHandler;

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

    private final Map<Pod, Bootstrap> testApiClients;
    private final Map<Pod, Bootstrap> dockerClients;

    private final Logger logger = LoggerFactory.getLogger("LoadBalancer(NettyServer)");

    public NettyServer(
            List<Pod> podList,
            CheckerService checkerService
    ) {
        this.checkerService = checkerService;

        // init clients
        Map<Pod, String> containerIdMap = new DockerContainerReader().getDockerContainerIdMap(podList);

        dockerClients = new HashMap<>();
        testApiClients = new HashMap<>(podList.size());

        for (Pod pod : podList) {
            testApiClients.put(pod, makeClient(
                    pod.getServer().getHost(),
                    pod.getPort(),
                    new TestApiReceiverHandler(pod, checkerService)
            ));
            dockerClients.put(pod, makeClient(
                    new Host("yacht.r-e.kr"), new Port(2375),
                    new DockerReceiverHandler(pod, containerIdMap.get(pod), checkerService)
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
        for(Pod pod : dockerClients.keySet()) {
            ChannelFuture channelFuture = dockerClients.get(pod).connect();

            channelFuture.addListener(future -> {
                if(channelFuture.isSuccess()) {
                    sendClients.add(channelFuture.channel());
                } else {
                    logger.info(String.format("Docker status connection fail server %s pod %s", pod.getServer().getName(), pod.getName()));
                    checkerService.addStatus(pod, ServiceStatus.DEAD, StatusType.Docker);
                    channelFuture.channel().close();
                }
            });
        }

        for(Pod pod : testApiClients.keySet()) {
            ChannelFuture channelFuture = testApiClients.get(pod).connect();

            channelFuture.addListener(future -> {
                if (channelFuture.isSuccess()) {
                    channelFuture.channel().attr(NettyServerContext.sendTimeAttr).set(LocalDateTime.now());
                    sendClients.add(channelFuture.channel());
                } else {
                    logger.info(String.format("TestApi status connection fail server %s pod %s", pod.getServer().getName(), pod.getName()));
                    checkerService.addStatus(pod, ServiceStatus.DEAD, StatusType.TestApi);
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

        public Map<Pod, String> getDockerContainerIdMap(List<Pod> podList) {
            List<Map<String, Object>> dockerPsResponse = getContainerIdMap();
            Map<Pod, String> containerIdMap = toDockerContainerId(podList, dockerPsResponse);

            return containerIdMap;
        }

        private List<Map<String, Object>> getContainerIdMap() {
            String response;
            try {
                // send docker ps api
                HttpRequest httpRequest =
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://yacht.r-e.kr:2375/containers/json?all=ture"))
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
                e.printStackTrace();
                return List.of();
            }
        }

        private Map<Pod, String> toDockerContainerId(
                List<Pod> podList,
                List<Map<String, Object>> dockerPsResponse
        ) {
            Map<Pod, String> containerIdMap = new HashMap<>();
            for(Pod pod : podList)
                containerIdMap.put(pod, getContainerId(pod, dockerPsResponse));

            return containerIdMap;
        }

        private String getContainerId(
                Pod pod,
                List<Map<String, Object>> dockerPsResponse
        ) {
            for(Map<String, Object> container : dockerPsResponse) {
                String containerName = ((List<String>)container.get("Names")).get(0);

                if(containerName.equals("/" + pod.getName()))
                    return (String) container.get("Id");
            }
            logger.error(pod.getName() + " : ContainerId not found!");
            return null; // 추후 DockerReceiverHandler에서 fail하게 됨
        }
    }

}
