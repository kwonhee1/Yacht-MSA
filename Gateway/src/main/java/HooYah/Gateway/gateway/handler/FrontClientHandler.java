package HooYah.Gateway.gateway.handler;

import HooYah.Gateway.gateway.AttributeConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontClientHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Logger logger = LoggerFactory.getLogger(FrontClientHandler.class);
    private final Pattern pattern;

    public FrontClientHandler() {
        super(); // release 안하면 메모리 누수 발생 가능!
        pattern = Pattern.compile("[\\n\\r\\t]");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String toHost = ctx.channel().attr(AttributeConfig.Host).get();
        int toPort = ctx.channel().attr(AttributeConfig.Port).get();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .remoteAddress(toHost, toPort)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpObjectAggregator(1048576)); // 최대 1MB
                        p.addLast(new BackendHandler(ctx.channel(), msg));
                    }
                });

        ChannelFuture future = bootstrap.connect();
        future.addListener((ChannelFutureListener) f -> {
            if (!f.isSuccess()) {
                ctx.close();
            }
        });

        ByteBuf requestBodyBuffer = msg.content();
        byte[] requestBody = new byte[requestBodyBuffer.capacity()];
        for (int i = 0; i < requestBodyBuffer.capacity(); i ++) {
            requestBody[i] = requestBodyBuffer.getByte(i);
        }

        logger.info(String.format("request send uri:{%s}, method:{%s} now:{%s}, data:{%s}",
                msg.uri(), msg.method().toString(), LocalDateTime.now().toString(), pattern.matcher(new String(requestBody)).replaceAll("")
        ));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}