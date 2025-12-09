package HooYah.Yacht;

import HooYah.Yacht.user.JWTUtil;
import io.jsonwebtoken.JwtException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class Router {

    private final JdbcTemplate jdbcTemplate;

    @Value("${USER_SERVER_PATH}")
    private String USER_SERVER_PATH;

    @Value("${USER_SERVER_URI}")
    private String USER_SERVER_URI;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user_route", new Function<PredicateSpec, Buildable<Route>>() {
                    @Override
                    public Buildable<Route> apply(PredicateSpec predicateSpec) {
                        return predicateSpec
                                .path(USER_SERVER_PATH)
                                .filters(f->
                                        f.filter(new TokenFilter())
                                                .filter(new LogFilter())
                                )
                                .uri(USER_SERVER_URI);
                    }
                })
                .build();
    }

    class LogFilter implements GatewayFilter {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();
            Long userId = (Long) request.getAttributes().get("userId");

            if(userId != null)
                log.info("user Id : " + userId);
            else
                log.info("no token");

            log.info("uri : " + request.getURI().toString());
            log.info("method : " + request.getMethod().toString());
            log.info("headers : " + request.getHeaders().toString());

            return chain.filter(exchange);
        }
    }

    class TokenFilter implements GatewayFilter {
        @Override
        public Mono<Void> filter(
                ServerWebExchange exchange,
                GatewayFilterChain chain
        ) {
            ServerHttpRequest request = exchange.getRequest();

            Optional<Long> userId = getUserIdFromToken(request);
            if(userId.isPresent() && validateUserIdFromDb(jdbcTemplate, userId.get())) {
                request = request.mutate().header("userId", userId.get().toString()).build();
                // request.getAttributes().put("userId", userId.get()); // .setAttribute("userId", userId.get());
            }else {
                // todo : request 에서 token지우기 or response에 빈 token 발급하기
            }

            return chain.filter(exchange.mutate().request(request).build());
        }

        private boolean validateUserIdFromDb(JdbcTemplate jdbcTemplate, Long userId) {
            Boolean isExist = jdbcTemplate.query(
                    "select 1 from user where id = ?",
                    (ps)->ps.setLong(1, userId),
                    (rs)->(Boolean)rs.next()
            );

            return isExist;
        }

        private Optional<Long> getUserIdFromToken(ServerHttpRequest request) {
            Optional<String> token = getTokenFromBearer(request);

            if(token.isEmpty()){
                return Optional.empty();
            }

            try{
                Long userId = JWTUtil.decodeToken(token.get());
                return Optional.of(userId);
            } catch(JwtException e){
                e.printStackTrace();
                return Optional.empty();
            }
        }

        private Optional<String> getTokenFromBearer(ServerHttpRequest request) {
            List<String> authHeaders = request.getHeaders().get("Authorization"); // .getHeader("Authorization");

            if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.getFirst().startsWith("Bearer ")) {
                return Optional.empty();
            }

            String token = authHeaders.getFirst().substring(7);
            return Optional.of(token);
        }
    }

//    @Bean
//    public RouterFunction<ServerResponse> proxyRouter() {
//        return RouterFunctions.route()
//                .filter(new TokenHandlerFunction())
//                .add(RouterFunctions.route(RequestPredicates.HEAD("uri"), new ProxyHandlerFunction()))
//                .build();
//    }
//
//    class ProxyHandlerFunction implements HandlerFunction<ServerResponse> {
//        @Override
//        public ServerResponse handle(ServerRequest request) throws Exception {
//            String uri = request.servletRequest().getRequestURI();
//
//            URI moduleURI = URI.create(uri);
//            return ServerResponse.temporaryRedirect(moduleURI).build();
//        }
//    }
//
//    @Slf4j
//    class TokenHandlerFunction implements HandlerFilterFunction {
//        @Override
//        public ServerResponse filter(ServerRequest request, HandlerFunction next) throws Exception {
//            Optional<Long> userId = getUserIdFromToken(request.servletRequest());
//
//            if(userId.isPresent() && validateUserIdFromDb(jdbcTemplate, userId.get())) {
//                request.servletRequest().setAttribute("userId", userId.get());
//                log.info("token success + " + userId.get());
//            } else {
//                // todo : response 에 빈 token 추가해서 잘못된 token 지우기? (고민 필요)
//            }
//
//            return next.handle(request);
//        }
//
//
//    }

}
