package HooYah.Gateway.locabalancer.conf;

import HooYah.Gateway.gateway.AttributeConfig;
import java.io.IOException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

    private static final Config instance = new Config();

    private ServerConfig serverConfig;
    private AttributeConfig attributeConfig = new AttributeConfig();

    public static Config getInstance() {
        return instance;
    }

    private Config() {
        Logger logger = LoggerFactory.getLogger(Config.class);

        logger.info("Try Load Server Config");
        serverConfig = new ServerConfig();
        logger.info("Load Server Config Success");
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

}
