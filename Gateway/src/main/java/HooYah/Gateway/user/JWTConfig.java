package HooYah.Gateway.user;

import HooYah.Gateway.config.ConfigFile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JWTConfig {

    private final static JWTConfig instance = new JWTConfig();

    private final JWTService jwtService;

    public static JWTService getJwtService() {
        return instance.jwtService;
    }

    private JWTConfig() {
        TokenConfigProperty tokenConfigProperty = ConfigFile.APPLICATION_PROPERTIES.getValue(TokenConfigProperty.class, "token");
        this.jwtService = new JWTService(tokenConfigProperty.getSecretKey(), tokenConfigProperty.getExpirationSecond());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class TokenConfigProperty {

        @JsonProperty("SECRET_KEY")
        private String secretKey;
        @JsonProperty("EXPIRATION_SECOND")
        private Long expirationSecond;

        public TokenConfigProperty() {}

        public String getSecretKey() {
            return secretKey;
        }

        public Long getExpirationSecond() {
            return expirationSecond;
        }

    }
}
