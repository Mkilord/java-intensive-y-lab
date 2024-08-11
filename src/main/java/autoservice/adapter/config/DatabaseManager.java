package autoservice.adapter.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseManager {
    private static final String URL = DatabaseConfig.getUrl();
    private static final String USER = DatabaseConfig.getUsername();
    private static final String PASSWORD = DatabaseConfig.getPassword();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
