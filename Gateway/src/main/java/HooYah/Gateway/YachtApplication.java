package HooYah.Gateway;

import HooYah.Gateway.handler.FrontClientHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YachtApplication {

    private static final Logger log = LoggerFactory.getLogger(YachtApplication.class);

    static final int LOCAL_PORT = Integer.parseInt(System.getProperty("localPort", "8443"));

    public static void main(String[] args) {
        new YachtApplication().run(LOCAL_PORT);
    }

    public void run(int port) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(group)
                    .channel(NioServerSocketChannel.class) // (3)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                             p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(1048576)); // 최대 1MB
//                             p.addLast(new HttpContentCompressor((CompressionOptions[]) null)); // 얘까지는 필수 (Http 통신을 위함)
//                             p.addLast(new HttpServerExpectContinueHandler()); // 이놈은 뭘하는지 모르겠음
                            p.addLast(new FrontClientHandler());
                        }
                    })
                    .childOption(ChannelOption.AUTO_READ, false);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            group.shutdownGracefully();
        }
    }
}
