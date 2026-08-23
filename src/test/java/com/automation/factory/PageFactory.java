package com.automation.factory;

import com.automation.config.ConfigManager;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * Creates BrowserContext and Page with the correct settings
 * for video recording, viewport, and tracing.
 *
 * WHY Browser → Context → Page?
 *   Browser  = the browser application (Chromium, Firefox, etc.)
 *   Context  = an isolated browser session (like incognito)
 *             — each context has its own cookies, storage, cache
 *   Page     = a single tab/window inside a context
 *
 * We create a NEW context per test method so tests are fully isolated.
 */
public class PageFactory {

    private static final Logger log = LoggerFactory.getLogger(PageFactory.class);

    private PageFactory() {
        // Utility class
    }

    /**
     * Creates a BrowserContext with optional video recording.
     */
    public static BrowserContext createContext(Browser browser) {
        ConfigManager config = ConfigManager.getInstance();

        var contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080);

        // Enable video recording when configured
        if (config.isVideoEnabled()) {
            contextOptions.setRecordVideoDir(Paths.get("target/videos/"));
            contextOptions.setRecordVideoSize(1280, 720);
            log.debug("Video recording enabled");
        }

        BrowserContext context = browser.newContext(contextOptions);

        // Start tracing if enabled — captures screenshots, DOM snapshots, and source
        if (config.isTraceEnabled()) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(false));
            log.debug("Tracing started");
        }

        return context;
    }

    /**
     * Creates a new Page (tab) from the given context.
     */
    public static Page createPage(BrowserContext context) {
        Page page = context.newPage();
        log.debug("New page created");
        return page;
    }
}
