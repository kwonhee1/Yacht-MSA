package HooYah.Gateway.user.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DBConfigProperty {

    @JsonProperty("DB_driver-class-name")
    private String driverClassName;
    @JsonProperty("DB_URL")
    private String url;
    @JsonProperty("DB_USERNAME")
    private String username;
    @JsonProperty("DB_PASSWORD")
    private String password;

    @JsonProperty("DB_pool-max-size")
    private String poolMaxSize;

    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(Integer.parseInt(poolMaxSize));

        return new HikariDataSource(config);
    }

}
