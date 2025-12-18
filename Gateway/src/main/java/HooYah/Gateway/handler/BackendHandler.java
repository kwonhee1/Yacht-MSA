package HooYah.Gateway.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackendHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger log = LoggerFactory.getLogger(BackendHandler.class);

    private final Channel inboundChannel;
    private final Object inputData;

    public BackendHandler(Channel inboundChannel, Object inputData) {
        super(false);
        this.inboundChannel = inboundChannel;
        this.inputData = inputData;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        if (inboundChannel.isActive()) {
            inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (ctx.channel().isActive()) {
                    ctx.channel().close();
                }
            });
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(inputData);
        ctx.read();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (inboundChannel.isActive()) {
            FrontClientHandler.closeOnFlush(inboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (inboundChannel.isActive()) {
            FrontClientHandler.closeOnFlush(inboundChannel);
        }
        if (ctx.channel().isActive()) {
            ctx.channel().close();
        }
    }

}