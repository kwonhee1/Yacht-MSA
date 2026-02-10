package HooYah.Gateway.loadbalancer.checker.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.time.LocalDateTime;

public class NettyServerContext {

    public static final AttributeKey<LocalDateTime> sendTimeAttr = AttributeKey.valueOf("sendTime");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}
