//package HooYah.Gateway.router.filter;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//public class LogFilter implements GatewayFilter {
//
//    Log logger = LogFactory.getLog(LogFilter.class);
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        Long userId = (Long) request.getAttributes().get("userId");
//
//        if(userId != null)
//            logger.info("user Id : " + userId);
//        else
//            logger.info("no token");
//
//        logger.info("uri : " + request.getURI().toString());
//        logger.info("method : " + request.getMethod().toString());
//        logger.info("headers : " + request.getHeaders().toString());
//
//        return chain.filter(exchange);
//    }
//}