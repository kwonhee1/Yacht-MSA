package HooYah.Gateway.user.db;

import HooYah.Gateway.config.ConfigFile;
import javax.sql.DataSource;

public class DBConfig {

    private static final DBConfig instance = new DBConfig();

    private DataSource dataSource;

    public static DataSource getDataSource() {
        return instance.dataSource;
    }

    private DBConfig() {
        DBConfigProperty config = ConfigFile.APPLICATION_PROPERTIES.getValue(DBConfigProperty.class);
        this.dataSource = config.dataSource();
    }

}