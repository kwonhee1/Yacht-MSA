package HooYah.Gateway.gateway.handler;

import HooYah.Gateway.user.JWTConfig;
import HooYah.Gateway.user.JWTService;
import HooYah.Gateway.user.UserService;
import io.jsonwebtoken.JwtException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger logger = LoggerFactory.getLogger(TokenHandler.class);
    private final JWTService jwtService = JWTConfig.getJwtService();
    private final UserService userService = new UserService();

    public TokenHandler () {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        setUserId(msg);

        ctx.fireChannelRead(msg);
    }

    private void setUserId(FullHttpRequest msg) {
        String authHeaders = msg.headers().get("Authorization");

        if (authHeaders == null || !authHeaders.startsWith("Bearer ")) {
            logger.info("no Authorization header");
            return;
        }

        String token = authHeaders.substring(7);
        Optional<Long> userIdOpt = getUserIdFromToken(token);

        if (userIdOpt.isEmpty()) {
            logger.info("invalid token");
            return;
        }

        Long userId =  userIdOpt.get();

        if(!userService.validateUserIdFromDb(userId)){
            logger.info("no user id from db " + userId);
            return;
        }

        logger.info("userId : " + userId);
        msg.headers().set("UserId", userId);
        msg.headers().set("Authorization", ""); // can not set null : NullPointerException
    }

    private Optional<Long> getUserIdFromToken(String token) {
        if(token == null || token.isEmpty())
            return Optional.empty();
        try {
            Long userId = jwtService.decodeToken(token);
            return Optional.of(userId);
        } catch (JwtException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
