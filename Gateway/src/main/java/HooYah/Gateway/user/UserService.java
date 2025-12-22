package HooYah.Gateway.user;

import HooYah.Gateway.user.db.DBConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

public class UserService {

    private final DataSource dataSource = DBConfig.getDataSource();

    private final String SELECT_USER_QUERY = "select 1 from user where user.id = ?";

    public boolean validateUserIdFromDb(Long userId) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(SELECT_USER_QUERY);
            preparedStatement.setLong(1, userId);

            ResultSet result = preparedStatement.executeQuery();

            return result.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
