package HooYah.Gateway;

import HooYah.Gateway.config.ApplicationConfig;
import HooYah.Gateway.user.JWTConfig;
import HooYah.Gateway.user.db.DBConfig;
import HooYah.Gateway.gateway.handler.URIHandler;
import HooYah.Gateway.loadbalancer.conf.Config;
import HooYah.Gateway.gateway.handler.FrontClientHandler;
import HooYah.Gateway.gateway.handler.TokenHandler;
import HooYah.Gateway.loadbalancer.controller.LoadBalancerController;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YachtApplication {

    private static final Logger log = LoggerFactory.getLogger(YachtApplication.class);

    private final LoadBalancerController loadBalancerController;

    public YachtApplication() {
        Config config = Config.getInstance();

        loadBalancerController = new LoadBalancerController(
                config.getServerConfig().getServers(),
                config.getServerConfig().getModules()
        );
    }

    public static void main(String[] args) {
        Config.getInstance();
        DBConfig.getDataSource();
        JWTConfig.getJwtService();
        new YachtApplication().run(new ApplicationConfig().getPort());
    }

    private void run(int port) {
        EventLoopGroup serverGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap(); // (2)
            serverBootstrap.group(serverGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(1048576)); // 최대 1MB
                            p.addLast(new TokenHandler());
                            p.addLast(new URIHandler(loadBalancerController));
//                             p.addLast(new HttpContentCompressor((CompressionOptions[]) null)); // 얘까지는 필수 (Http 통신을 위함)
//                             p.addLast(new HttpServerExpectContinueHandler()); // 이놈은 뭘하는지 모르겠음
                            p.addLast(new FrontClientHandler());
                        }
                    })
                    // .childOption(ChannelOption.AUTO_READ, false)
            ;

            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            serverGroup.shutdownGracefully();
        }
    }
}
