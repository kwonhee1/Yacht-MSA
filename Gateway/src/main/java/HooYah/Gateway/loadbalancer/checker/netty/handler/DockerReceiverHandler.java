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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class DockerReceiverHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger log = LoggerFactory.getLogger("LoadBalancer(DockerHandler)");

    private final Pod pod;
    private final CheckerService checkerService;
    private final ObjectMapper objectMapper = NettyServerContext.getObjectMapper();

    private String containerId;

                public DockerReceiverHandler(
                        Pod pod,
                        String containerId,
                        CheckerService checkerService
                ) {
                    this.pod = pod;        this.checkerService = checkerService;

        this.containerId = containerId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Docker Receiver Channel active");

        String uri = String.format("/containers/%s/stats?stream=false", containerId);
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
        ctx.writeAndFlush(request).addListener(f -> {
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
        ServiceStatus testApiStatus;
        if(msg.status().code() != 200) {
            testApiStatus = ServiceStatus.UNKNOWN;
        } else {
            String dockerStatusResponse = msg.content().toString(CharsetUtil.UTF_8);
            int pids = getPidsFromDockerResponse(dockerStatusResponse);

            if(pids == -1) // unknown
                testApiStatus = ServiceStatus.UNKNOWN;
            else if (pids < 100)
                testApiStatus = ServiceStatus.GOOD;
            else if (pids < 200)
                testApiStatus = ServiceStatus.NORMAL;
            else
                testApiStatus = ServiceStatus.BAD;
        }

        checkerService.addStatus(pod, testApiStatus, StatusType.Docker);

        ctx.channel().close();
    }

    private int getPidsFromDockerResponse(String dockerResponse) {
        try {
            Map responseMap = objectMapper.readValue(dockerResponse, Map.class);
            return (int) ((Map) responseMap.get("pids_stats")).get("current");
        } catch (JsonProcessingException e) {
            log.info("Docker Receiver Handler :: json parsing error");
        } catch (ClassCastException | NullPointerException e) {
            log.info("Docker Receiver Handler :: class casting error");
        }

        return -1;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
