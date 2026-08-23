package com.automation.factory;

import com.automation.config.ConfigManager;
import com.automation.enums.BrowserType;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launches the correct browser engine based on configuration.
 *
 * WHY: Centralizes browser selection logic.
 * Supported: Chromium (default), Firefox, WebKit.
 *
 * HOW IT WORKS:
 *   BrowserType enum → Playwright.chromium()/firefox()/webkit() → Browser
 */
public class BrowserFactory {

    private static final Logger log = LoggerFactory.getLogger(BrowserFactory.class);

    private BrowserFactory() {
        // Utility class
    }

    /**
     * Launches a browser using the current Playwright instance.
     *
     * @param playwright the Playwright instance
     * @param browserType which browser engine to launch
     * @param headless true for headless mode, false for headed (visible) mode
     * @return launched Browser instance
     */
    public static Browser createBrowser(Playwright playwright, BrowserType browserType, boolean headless) {
        var launchOptions = new com.microsoft.playwright.BrowserType.LaunchOptions()
                .setHeadless(headless);

        Browser browser = switch (browserType) {
            case FIREFOX -> playwright.firefox().launch(launchOptions);
            case WEBKIT -> playwright.webkit().launch(launchOptions);
            default -> playwright.chromium().launch(launchOptions);
        };

        log.info("Browser launched: {} (headless: {})", browserType.getName(), headless);
        return browser;
    }

    /**
     * Convenience method using ConfigManager settings.
     */
    public static Browser createBrowser(Playwright playwright) {
        ConfigManager config = ConfigManager.getInstance();
        return createBrowser(playwright, config.getBrowser(), config.isHeadless());
    }
}
