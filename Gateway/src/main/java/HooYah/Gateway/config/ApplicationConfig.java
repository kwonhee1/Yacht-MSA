package HooYah.Gateway.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationConfig {

    private int port;

    public ApplicationConfig() {
        PortProperty portProperty = ConfigFile.APPLICATION_PROPERTIES.getValue(PortProperty.class);
        this.port = portProperty.getPort();
    }

    public int getPort() {
        return port;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PortProperty {

        @JsonProperty("port")
        String port;

        public int getPort() {
            return Integer.parseInt(port);
        }

    }


}
