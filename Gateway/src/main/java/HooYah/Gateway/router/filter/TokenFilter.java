//package HooYah.Gateway.router.filter;
//
//import HooYah.Gateway.user.JWTUtil;
//import io.jsonwebtoken.JwtException;
//import java.util.List;
//import java.util.Optional;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//public class TokenFilter implements GatewayFilter {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public TokenFilter(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @Override
//    public Mono<Void> filter(
//            ServerWebExchange exchange,
//            GatewayFilterChain chain
//    ) {
//        ServerHttpRequest request = exchange.getRequest();
//
//        Optional<Long> userId = getUserIdFromToken(request);
//        if (userId.isPresent() && validateUserIdFromDb(jdbcTemplate, userId.get())) {
//            request = request.mutate().header("userId", userId.get().toString()).build();
//            // request.getAttributes().put("userId", userId.get()); // .setAttribute("userId", userId.get());
//        } else {
//            // todo : request 에서 token지우기 or response에 빈 token 발급하기
//        }
//
//        return chain.filter(exchange.mutate().request(request).build());
//    }
//
//    private boolean validateUserIdFromDb(JdbcTemplate jdbcTemplate, Long userId) {
//        Boolean isExist = jdbcTemplate.query(
//                "select 1 from user where id = ?",
//                (ps) -> ps.setLong(1, userId),
//                (rs) -> (Boolean) rs.next()
//        );
//
//        return isExist;
//    }
//
//    private Optional<Long> getUserIdFromToken(ServerHttpRequest request) {
//        Optional<String> token = getTokenFromBearer(request);
//
//        if (token.isEmpty()) {
//            return Optional.empty();
//        }
//
//        try {
//            Long userId = JWTUtil.decodeToken(token.get());
//            return Optional.of(userId);
//        } catch (JwtException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }
//
//    private Optional<String> getTokenFromBearer(ServerHttpRequest request) {
//        List<String> authHeaders = request.getHeaders().get("Authorization"); // .getHeader("Authorization");
//
//        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.getFirst().startsWith("Bearer ")) {
//            return Optional.empty();
//        }
//
//        String token = authHeaders.getFirst().substring(7);
//        return Optional.of(token);
//    }
//}