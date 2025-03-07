package org.bookmyshow.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigLoader {
    private static final Logger logger = Logger.getLogger(ConfigLoader.class.getName());
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading config.properties", e);
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
