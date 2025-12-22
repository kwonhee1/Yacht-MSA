package HooYah.Gateway.gateway.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackendHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger log = LoggerFactory.getLogger(BackendHandler.class);

    private final Channel inboundChannel;
    private final FullHttpRequest inputData;

    public BackendHandler(Channel inboundChannel, FullHttpRequest inputData) {
        super(false);
        this.inboundChannel = inboundChannel;
        this.inputData = inputData.copy().retain();// inputData.retainedDuplicate(); //inputData.copy().retain();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        log.info("proxy success response : " + msg.status().code() + ", body : " + msg.content().toString(CharsetUtil.UTF_8));

        if (inboundChannel.isActive()) {
            inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                ctx.channel().close();
                future.channel().close();
            });
        }

        inputData.release(); // copy해서 들어온 값 release
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        inboundChannel.close();
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(inputData).addListener(f -> {
            if (f.isSuccess()) {
                ctx.read();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}