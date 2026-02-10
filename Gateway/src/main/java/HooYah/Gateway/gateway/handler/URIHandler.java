package HooYah.Gateway.gateway.handler;

import HooYah.Gateway.gateway.AttributeConfig;
import HooYah.Gateway.loadbalancer.LoadBalancer;
import HooYah.Gateway.loadbalancer.domain.vo.Url;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final LoadBalancer loadBalancer;

    public URIHandler(LoadBalancer loadBalancer) {
        super(false);
        this.loadBalancer = loadBalancer;
    }

    private Logger logger = LoggerFactory.getLogger(URIHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String requestUri = msg.uri();

        Url proxy = loadBalancer.loadBalance(requestUri);
        String proxyHost = proxy.getHost().getHost();
        int proxyPort = proxy.getPort().getPort();
        // String proxyUri = proxy.getUri().toProxyUri(new Uri(requestUri));
        String proxyUri = proxy.getUri().getUri();

        ctx.channel().attr(AttributeConfig.Host).set(proxyHost);
        ctx.channel().attr(AttributeConfig.Port).set(proxyPort);
        msg.setUri(proxyUri);

        logger.info("proxy " + requestUri + " -> " + proxyHost + ":" + proxyPort + proxyUri);

        ctx.fireChannelRead(msg);
    }
}
