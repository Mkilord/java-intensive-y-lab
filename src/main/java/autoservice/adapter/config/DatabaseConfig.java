package autoservice.adapter.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class DatabaseConfig {

    private static Properties properties;
    public static final String DEFAULT_FILE_PATH = "src/main/resources/database.properties";

    public static Properties loadProperties() throws IOException {
        if (Objects.nonNull(properties)) return properties;
        properties = new Properties();
        try (InputStream input = new FileInputStream(DEFAULT_FILE_PATH)) {
            properties.load(input);
        }
        return properties;
    }

    private static String getEnvVariable(String name) {
        try {
            return Optional.ofNullable(loadProperties().getProperty(name))
                    .orElseThrow(() -> new IllegalArgumentException("Environment variable " + name + " not found!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUrl() {
        return getEnvVariable("url");
    }

    public static String getUsername() {
        return getEnvVariable("username");
    }

    public static String getPassword() {
        return getEnvVariable("password");
    }

    public static String getDriver() {
        return getEnvVariable("driver");
    }

}

