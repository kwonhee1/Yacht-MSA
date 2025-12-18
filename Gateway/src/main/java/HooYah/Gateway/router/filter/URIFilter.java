//package HooYah.Gateway.router.filter;
//
//import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
//
//import HooYah.Gateway.domain.module.Modules;
//import HooYah.Gateway.domain.vo.Api;
//import HooYah.Gateway.domain.vo.Uri;
//import HooYah.Gateway.domain.vo.Url;
//import java.net.URI;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//public class URIFilter implements GatewayFilter {
//
//    private final Modules modules;
//
//    private Log logger = LogFactory.getLog(URIFilter.class);
//
//    public URIFilter(Modules modules) {
//        this.modules = modules;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        Uri requestUri = Uri.from(exchange.getRequest().getURI());
//        Url loadedUrl = modules.loadRequest(requestUri);
//
//        URI loadedURI = new Api(loadedUrl, requestUri).toURI();
//        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, loadedURI);
//
//        logger.info("URIFilter :: requestURI "+ requestUri +", toUrl " + loadedUrl);
//
//        return chain.filter(exchange);
//    }
//}