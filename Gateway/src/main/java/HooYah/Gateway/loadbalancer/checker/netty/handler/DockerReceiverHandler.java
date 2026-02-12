package HooYah.Gateway.loadbalancer.checker.netty.handler;

import HooYah.Gateway.loadbalancer.checker.CheckerService.StatusType;
import HooYah.Gateway.loadbalancer.checker.netty.NettyServerContext;
import HooYah.Gateway.loadbalancer.checker.status.ServiceStatus;
import HooYah.Gateway.loadbalancer.domain.pod.Pod;
import HooYah.Gateway.loadbalancer.checker.CheckerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class DockerReceiverHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger log = LoggerFactory.getLogger("LoadBalancer(DockerHandler)");

    private final Pod pod;
    private final CheckerService checkerService;
    private final ObjectMapper objectMapper = NettyServerContext.getObjectMapper();

    private final FullHttpRequest request;

    public DockerReceiverHandler(
        Pod pod,
        String containerId,
        CheckerService checkerService
    ) {
        this.pod = pod;
        this.checkerService = checkerService;

        String requestUri = String.format("/containers/%s/stats?stream=false", containerId);
        request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, requestUri);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("DockerReceiverHandler Active! send DockerApi ("+ pod.getName() +") to " + ctx.channel().remoteAddress());

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
        // 반환 값을 확인한다
        ServiceStatus dockerStatus;
        if(msg.status().code() != 200) {
            dockerStatus = ServiceStatus.UNKNOWN;
        } else {
            String dockerStatusResponse = msg.content().toString(CharsetUtil.UTF_8);
            int pids = getPidsFromDockerResponse(dockerStatusResponse);

            if(pids == -1) // unknown
                dockerStatus = ServiceStatus.UNKNOWN;
            else if (pids < 100)
                dockerStatus = ServiceStatus.GOOD;
            else if (pids < 200)
                dockerStatus = ServiceStatus.NORMAL;
            else
                dockerStatus = ServiceStatus.BAD;
        }

        log.info(pod.getName() + " : DockerStatusResult : " + dockerStatus);
        checkerService.addStatus(pod, dockerStatus, StatusType.Docker);

        ctx.channel().close();
    }

    private int getPidsFromDockerResponse(String dockerResponse) {
        try {
            Map responseMap = objectMapper.readValue(dockerResponse, Map.class);
            return (int) ((Map) responseMap.get("pids_stats")).get("current");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("Docker Receiver Handler :: json parsing error");
        } catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            log.error("Docker Receiver Handler :: class casting error");
        }

        return -1;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
