package autoservice.adapter.config;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class DatabaseConfig {

    private static Properties properties;
    private static final String DEFAULT_FILE_PATH = "database.properties";

    public static Properties loadProperties() throws IOException {
        if (Objects.nonNull(properties)) return properties;
        properties = new Properties();

        @Cleanup
        var input = DatabaseConfig.class.getClassLoader().getResourceAsStream(DEFAULT_FILE_PATH);
        if (input == null) {
            throw new FileNotFoundException("Property file '" + DEFAULT_FILE_PATH + "' not found in the classpath");
        }
        properties.load(input);

        return properties;
    }

    @SneakyThrows(IOException.class)
    private static String getEnvVariable(String name) {
        return Optional.ofNullable(loadProperties().getProperty(name))
                .orElseThrow(() -> new IllegalArgumentException("Environment variable " + name + " not found!"));
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

