package HooYah.Gateway.gateway.handler;

import HooYah.Gateway.gateway.AttributeConfig;
import HooYah.Gateway.loadbalancer.domain.vo.Url;
import HooYah.Gateway.provider.ProxyProvider;
import HooYah.Gateway.provider.Resource;
import HooYah.Gateway.provider.TooManyRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final ProxyProvider proxyProvider;

    public URIHandler(ProxyProvider proxyProvider) {
        super(false);
        this.proxyProvider = proxyProvider;
    }

    private Logger logger = LoggerFactory.getLogger("Netty(URIHandler) ");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String requestUri = msg.uri();

        Resource<Url> proxyResource;
        try {
            proxyResource = proxyProvider.provide(requestUri);
        } catch (TooManyRequest e) {
            logger.error("proxy fail(TooManyRequest) : " + requestUri);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.TOO_MANY_REQUESTS);

            // throws org.apache.http.NoHttpResponseException: yacht.r-e.kr:9090 failed to respond (when testing with apache jmeter)
            response.headers().set("Content-Type", "text/plain; charset=UTF-8");
            response.headers().set("Connection", "close");

            ctx.writeAndFlush(response);
            ctx.channel().close();
            return;
        }

        ctx.channel().attr(AttributeConfig.ProxyResource).set(proxyResource);

        // print log
        Url proxy = proxyResource.get();
        String proxyHost = proxy.getHost().getHost();
        int proxyPort = proxy.getPort().getPort();
        String proxyUri = proxy.getUri().getUri();

        msg.setUri(proxyUri);

        logger.info("proxy " + requestUri + " -> " + proxyHost + ":" + proxyPort + proxyUri);

        ctx.fireChannelRead(msg);
    }
}
