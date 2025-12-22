package HooYah.Gateway.gateway.handler;

import HooYah.Gateway.gateway.AttributeConfig;
import HooYah.Gateway.locabalancer.conf.Config;
import HooYah.Gateway.locabalancer.conf.ServerConfig;
import HooYah.Gateway.locabalancer.controller.LoadBalancerController;
import HooYah.Gateway.locabalancer.domain.vo.Uri;
import HooYah.Gateway.locabalancer.domain.vo.Url;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final LoadBalancerController loadBalancerController;

    public URIHandler(LoadBalancerController loadBalancerController) {
        super(false);
        this.loadBalancerController = loadBalancerController;
    }

    private Logger logger = LoggerFactory.getLogger(URIHandler.class);
    private final static ServerConfig serverConfig = Config.getInstance().getServerConfig();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String requestUri = msg.uri();

        Url proxy = loadBalancerController.loadBalance(requestUri);
        String proxyHost = proxy.getHost().getHost();
        int proxyPort = proxy.getPort().getPort();
        String proxyUri = proxy.getUri().toProxyUri(new Uri(requestUri));

        ctx.channel().attr(AttributeConfig.Host).set(proxyHost);
        ctx.channel().attr(AttributeConfig.Port).set(proxyPort);
        msg.setUri(proxyUri);

        logger.info("proxy " + requestUri + " -> " + proxyHost + ":" + proxyPort + proxyUri);

        ctx.fireChannelRead(msg);
    }
}
