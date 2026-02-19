package HooYah.Gateway.gateway;

import HooYah.Gateway.loadbalancer.domain.vo.Url;
import HooYah.Gateway.provider.Resource;
import io.netty.util.AttributeKey;

public class AttributeConfig {

    public static final AttributeKey<String> Host = AttributeKey.valueOf("Host");
    public static final AttributeKey<Integer> Port = AttributeKey.valueOf("Port");
    public static final AttributeKey<Resource<Url>> ProxyResource = AttributeKey.valueOf("ProxyResource");

}
