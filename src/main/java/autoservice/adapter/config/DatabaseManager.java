package autoservice.adapter.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseManager {
    @Setter
    private static String url = DatabaseConfig.getUrl();
    @Setter
    private static String user = DatabaseConfig.getUsername();
    @Setter
    private static String password = DatabaseConfig.getPassword();

    public static void setJdbcUrl(String newUrl) {
        url = newUrl;
    }

    public static void setUsername(String newUser) {
        user = newUser;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
