package com.automation.utils;

import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Screenshot utility methods.
 *
 * WHY: Provides reusable screenshot helpers used by TestHooks and tests.
 * Most screenshot capture is done automatically in TestHooks on failure,
 * but this utility is available for manual screenshots in tests.
 */
public class ScreenshotUtils {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "target/screenshots";

    private ScreenshotUtils() {}

    /**
     * Takes a full-page screenshot and returns the bytes.
     */
    public static byte[] captureScreenshot(Page page) {
        if (page == null || page.isClosed()) {
            log.warn("Cannot capture screenshot — page is null or closed");
            return new byte[0];
        }
        return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }

    /**
     * Takes a screenshot and saves it to a file.
     *
     * @param page the Playwright page
     * @param fileName name for the screenshot file (without directory)
     * @return the path where the screenshot was saved
     */
    public static Path captureAndSave(Page page, String fileName) {
        try {
            Path dir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(dir);

            Path filePath = dir.resolve(fileName);
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(filePath)
                    .setFullPage(true));

            log.info("Screenshot saved: {}", filePath);
            return filePath;

        } catch (Exception e) {
            log.error("Failed to save screenshot: {}", fileName, e);
            return null;
        }
    }
}
