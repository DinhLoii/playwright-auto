package com.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads .properties files from the classpath.
 *
 * WHY: Separates file-reading logic from configuration logic.
 * ConfigManager decides WHAT to read; ConfigReader handles HOW to read it.
 */
public class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);

    private ConfigReader() {
        // Utility class — no instances needed
    }

    /**
     * Loads a properties file from the classpath.
     *
     * @param filePath path relative to src/test/resources, e.g. "config/config.properties"
     * @return loaded Properties, or empty Properties if file not found
     */
    public static Properties loadProperties(String filePath) {
        Properties properties = new Properties();

        try (InputStream stream = ConfigReader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (stream == null) {
                log.warn("Config file not found on classpath: {}", filePath);
                return properties;
            }
            properties.load(stream);
            log.debug("Loaded config file: {} ({} properties)", filePath, properties.size());
        } catch (IOException e) {
            log.error("Failed to load config file: {}", filePath, e);
        }

        return properties;
    }
}
