package com.automation.enums;

/**
 * Supported browser types for Playwright.
 * Playwright supports three browser engines: Chromium, Firefox, and WebKit.
 */
public enum BrowserType {

    CHROMIUM("chromium"),
    FIREFOX("firefox"),
    WEBKIT("webkit");

    private final String name;

    BrowserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Resolves a browser type from a string value (case-insensitive).
     * Defaults to CHROMIUM if the value is null or unrecognized.
     */
    public static BrowserType fromString(String value) {
        if (value == null || value.isBlank()) {
            return CHROMIUM;
        }
        for (BrowserType type : values()) {
            if (type.name.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        return CHROMIUM;
    }
}
