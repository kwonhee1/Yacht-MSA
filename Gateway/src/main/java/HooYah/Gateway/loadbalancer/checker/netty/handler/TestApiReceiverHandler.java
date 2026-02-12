package HooYah.Gateway.loadbalancer.checker.netty.handler;

import HooYah.Gateway.loadbalancer.checker.CheckerService.StatusType;
import HooYah.Gateway.loadbalancer.checker.netty.NettyServerContext;
import HooYah.Gateway.loadbalancer.checker.status.ServiceStatus;
import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.checker.CheckerService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class TestApiReceiverHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger log = LoggerFactory.getLogger("LoadBalancer(TestApiHandler)");

    private final Pod pod;
    private final CheckerService checkerService;

    private static final String testApiURI = "/test";

    private final FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, testApiURI);

                public TestApiReceiverHandler(
                        Pod pod,
                        CheckerService checkerService
                ) {
                    this.pod = pod;        this.checkerService = checkerService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("TestApiReceiverHandler Active! send TestApi to " + ctx.channel().remoteAddress()+"/test");

        // set request header
        String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
        int port = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
        request.headers().set(HttpHeaderNames.HOST, host + ":" + port);

        ctx.writeAndFlush(request.retain()).addListener(f -> {
            if (f.isSuccess()) {
                ctx.read();
            } else {
                f.cause().printStackTrace();
                ctx.channel().close();
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        // 반환값을 확인한다
        ServiceStatus testApiStatus;
        if(msg.status().code() != 200) {
            testApiStatus = ServiceStatus.UNKNOWN;
            log.info(msg.status() + " , " + msg.content().toString(StandardCharsets.UTF_8));
        }else {
            LocalDateTime sendTime = ctx.channel().attr(NettyServerContext.sendTimeAttr).get();
            LocalDateTime receiveTime = LocalDateTime.now();

            long elapsedMilliSecond = Duration.between(sendTime, receiveTime).toMillis();

            if(elapsedMilliSecond > 1000)
                testApiStatus = ServiceStatus.BAD;
            else
                testApiStatus = ServiceStatus.GOOD;
        }

        // status 를 등록한다
        log.info(ctx.channel().remoteAddress() + " : TestApiResult : " + testApiStatus);
        checkerService.addStatus(pod, testApiStatus, StatusType.TestApi);

        // channel을 종료한다
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
