package org.bookmyshow.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads application properties from config.properties on the classpath.
 */
public final class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties not found on classpath.");
            }
            properties.load(input);
            logger.info("config.properties loaded successfully.");
        } catch (IOException e) {
            logger.error("Failed to load config.properties.", e);
            throw new ExceptionInInitializerError("Cannot load application configuration.");
        }
    }

    private ConfigLoader() {}

    /**
     * Returns the property value, or throws if not found.
     */
    public static String getProperty(final String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required configuration key: " + key);
        }
        return value.trim();
    }

    /**
     * Returns the property value, or the provided default if not found.
     */
    public static String getProperty(final String key, final String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }
}
