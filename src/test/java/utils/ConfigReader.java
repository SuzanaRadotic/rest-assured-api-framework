package utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    static {
        try (InputStream input =
                     ConfigReader.class.getClassLoader()
                             .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "Unable to find config.properties in classpath"
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load config.properties",
                    e
            );
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty(
                "base.url",
                "https://api-v4.practicesoftwaretesting.com"
        );
    }

    public static String getCustomerEmail() {
        String email = dotenv.get("CUSTOMER_EMAIL");

        if (email == null || email.isBlank()) {
            throw new RuntimeException(
                    "CUSTOMER_EMAIL is not set"
            );
        }

        return email;
    }

    public static String getCustomerPassword() {
        String password = dotenv.get("CUSTOMER_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new RuntimeException(
                    "CUSTOMER_PASSWORD is not set"
            );
        }

        return password;
    }
}