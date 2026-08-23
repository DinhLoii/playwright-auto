package com.automation.enums;

/**
 * Supported test environments.
 * Each enum value maps to a properties file: e.g. DEV → dev.properties
 */
public enum Environment {

    DEV("dev"),
    STAGING("staging");

    private final String name;

    Environment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Resolves an environment from a string value (case-insensitive).
     * Defaults to DEV if the value is null or unrecognized.
     */
    public static Environment fromString(String value) {
        if (value == null || value.isBlank()) {
            return DEV;
        }
        for (Environment env : values()) {
            if (env.name.equalsIgnoreCase(value.trim())) {
                return env;
            }
        }
        return DEV;
    }
}
