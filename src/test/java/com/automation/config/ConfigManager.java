package com.automation.config;

import com.automation.enums.BrowserType;
import com.automation.enums.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Central configuration manager for the framework.
 *
 * Resolution priority (highest to lowest):
 *   1. System property       — e.g. -Dbrowser=firefox
 *   2. Environment config    — e.g. staging.properties
 *   3. Default config        — config.properties
 *
 * WHY: A single source of truth for all configuration values.
 * Tests and factories call ConfigManager instead of reading properties directly.
 *
 * USAGE:
 *   ConfigManager config = ConfigManager.getInstance();
 *   String baseUrl = config.getBaseUrl();
 *   boolean headless = config.isHeadless();
 */
public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    private static final String DEFAULT_CONFIG = "config/config.properties";

    private static ConfigManager instance;

    private final Properties defaultProps;
    private final Properties envProps;

    private ConfigManager() {
        // 1. Load default configuration
        defaultProps = ConfigReader.loadProperties(DEFAULT_CONFIG);

        // 2. Determine active environment and load its config
        Environment env = getEnvironment();
        String envConfigFile = "config/" + env.getName() + ".properties";
        envProps = ConfigReader.loadProperties(envConfigFile);

        log.info("Configuration initialized — environment: {}", env.getName());
    }

    /**
     * Returns the singleton instance.
     * Thread-safe via synchronized — called once at startup, so no performance concern.
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Resolves a property value with priority:
     * System property → environment config → default config
     */
    public String getProperty(String key) {
        // Priority 1: System property (e.g. -Dbase.url=...)
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }

        // Priority 2: Environment-specific config
        String envValue = envProps.getProperty(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        // Priority 3: Default config
        String defaultValue = defaultProps.getProperty(key);
        return (defaultValue != null) ? defaultValue.trim() : null;
    }

    public String getProperty(String key, String fallback) {
        String value = getProperty(key);
        return (value != null) ? value : fallback;
    }

    // ================================================================
    // Typed convenience getters
    // ================================================================

    public Environment getEnvironment() {
        String envValue = System.getProperty("env",
                defaultProps.getProperty("env", "dev"));
        return Environment.fromString(envValue);
    }

    public String getBaseUrl() {
        return getProperty("base.url", "https://opensource-demo.orangehrmlive.com");
    }

    public BrowserType getBrowser() {
        return BrowserType.fromString(getProperty("browser", "chromium"));
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "true"));
    }

    public int getTimeout() {
        return Integer.parseInt(getProperty("timeout", "30000"));
    }

    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }

    public boolean isVideoEnabled() {
        return Boolean.parseBoolean(getProperty("video", "false"));
    }

    public boolean isTraceEnabled() {
        return Boolean.parseBoolean(getProperty("trace", "true"));
    }

    /**
     * Reads a credential with priority:
     * 1. Environment variable (for production/CI)
     * 2. Properties file (for dev environments)
     * 3. Fallback default
     *
     * WHY: Credentials for public demo sites can be in properties.
     * Production credentials must use environment variables.
     */
    public String getUsername() {
        String envVar = System.getenv("APP_USERNAME");
        if (envVar != null && !envVar.isBlank()) {
            return envVar;
        }
        return getProperty("app.username", "Admin");
    }

    public String getPassword() {
        String envVar = System.getenv("APP_PASSWORD");
        if (envVar != null && !envVar.isBlank()) {
            return envVar;
        }
        return getProperty("app.password", "admin123");
    }

    /**
     * Resets the singleton — used only in tests that need to reload config.
     */
    static synchronized void reset() {
        instance = null;
    }
}
