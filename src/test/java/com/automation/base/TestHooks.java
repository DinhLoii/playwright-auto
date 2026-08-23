package com.automation.base;

import com.automation.config.ConfigManager;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles test artifact capture (screenshots, traces, videos).
 *
 * WHY: Separates artifact logic from BaseTest lifecycle,
 * keeping BaseTest clean and focused on setup/teardown.
 *
 * Called from BaseTest's @AfterMethod when a test fails.
 */
public class TestHooks {

    private static final Logger log = LoggerFactory.getLogger(TestHooks.class);

    private TestHooks() {
        // Utility class
    }

    /**
     * Captures a screenshot of the current page state.
     *
     * @param page the Playwright Page
     * @param testName name of the failed test — used in filename
     * @return path to the saved screenshot, or null if capture failed
     */
    public static Path captureScreenshot(Page page, String testName) {
        if (page == null || page.isClosed()) {
            log.warn("Cannot capture screenshot — page is null or closed");
            return null;
        }

        try {
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);

            String fileName = sanitizeFileName(testName) + "_" + System.currentTimeMillis() + ".png";
            Path screenshotPath = dir.resolve(fileName);

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));

            log.info("Screenshot saved: {}", screenshotPath);
            return screenshotPath;

        } catch (Exception e) {
            log.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }

    /**
     * Stops tracing and saves the trace file.
     *
     * @param context the BrowserContext with active tracing
     * @param testName name of the test — used in filename
     * @return path to the saved trace file, or null if capture failed
     */
    public static Path captureTrace(BrowserContext context, String testName) {
        ConfigManager config = ConfigManager.getInstance();

        if (!config.isTraceEnabled() || context == null) {
            return null;
        }

        try {
            Path dir = Paths.get("target", "traces");
            Files.createDirectories(dir);

            String fileName = sanitizeFileName(testName) + "_" + System.currentTimeMillis() + ".zip";
            Path tracePath = dir.resolve(fileName);

            context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));

            log.info("Trace saved: {}", tracePath);
            return tracePath;

        } catch (Exception e) {
            log.error("Failed to capture trace for test: {}", testName, e);
            return null;
        }
    }

    /**
     * Removes characters that are unsafe in filenames.
     */
    private static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
