package HooYah.Gateway.checker.handler;

import HooYah.Gateway.loadbalancer.conf.Config;
import HooYah.Gateway.loadbalancer.domain.module.Modules;
import HooYah.Gateway.loadbalancer.domain.server.Server;
import HooYah.Gateway.loadbalancer.domain.service.Service;
import HooYah.Gateway.loadbalancer.domain.vo.Url;
import io.jsonwebtoken.impl.lang.Services;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.AttributeKey;
import java.util.ArrayList;
import java.util.List;

public class CheckServiceStatusService {

    private final Modules serviceList;
    private final List<Server> serverList;

    private final Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    public CheckServiceStatusService(Modules serviceList, List<Server> serverList) {
        this.serviceList = serviceList;
        this.serverList = serverList;

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
    }

    private void checkServiceStatus(List<Service> serviceList) {
        // ready to bootstrap
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpObjectAggregator(1048576)); // 최대 1MB
                        // p.addLast(new DockerStatusChecker());
                    }
                });

        // send all request :: service status check
        List<ChannelFuture> channelFutures = new ArrayList<>();
        for(Service service : serviceList) {
            String host = service.getServer().getHost().getHost();
            String uri = "uri";

            int port = service.getPort().getPort();

            ChannelFuture newChannel = bootstrap.connect(host + uri, port); // not contains protocol!

            channelFutures.add(newChannel);

            newChannel.addListener((ChannelFutureListener) future -> {
                if(!future.isSuccess()) {
                    future.cause().printStackTrace(); // log
                    channelFutures.remove(future);
                }
            });
        }

        // wait all response
        try {
            for(ChannelFuture channelFuture : channelFutures)
                channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

}
