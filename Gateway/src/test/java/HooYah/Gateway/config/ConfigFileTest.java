package HooYah.Gateway.config;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigFileTest {

    @Test
    public void ConfigFileInitTest () {
        Map property = ConfigFile.APPLICATION_PROPERTIES.getValue(Map.class);
        Assertions.assertEquals("com.mysql.cj.jdbc.Driver", property.get("DB_driver-class-name"));
    }

}
