package autoservice.adapter.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

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

    @SneakyThrows(ClassNotFoundException.class)
    public static Connection getConnection() throws SQLException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(url, user, password);
    }
}
