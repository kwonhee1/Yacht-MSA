package HooYah.Gateway.conf;

import lombok.Getter;

@Getter
public class Config {

    private static final Config instance = new Config();

    private ServerConfig serverConfig = new ServerConfig();

    public static Config getInstance() {
        return instance;
    }

    private Config() {}

}
