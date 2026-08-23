package com.automation.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Wait utility methods using Playwright's built-in waiting.
 *
 * WHY: Provides named wait methods for common patterns.
 * All methods delegate to Playwright's auto-waiting — NO Thread.sleep().
 *
 * In most cases, Playwright's Locator auto-waiting is sufficient.
 * Use these helpers only when you need explicit control.
 */
public class WaitUtils {

    private WaitUtils() {}

    /**
     * Waits for a locator to become visible.
     */
    public static void waitForVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
    }

    /**
     * Waits for a locator to become visible with a custom timeout.
     */
    public static void waitForVisible(Locator locator, double timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
    }

    /**
     * Waits for a locator to become hidden or detached.
     */
    public static void waitForHidden(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN));
    }

    /**
     * Waits for the page to reach the "load" state.
     */
    public static void waitForPageLoad(Page page) {
        page.waitForLoadState();
    }

    /**
     * Waits for the page URL to match a pattern (glob-style).
     */
    public static void waitForUrl(Page page, String urlPattern) {
        page.waitForURL(urlPattern);
    }

    /**
     * Waits for a network response matching the URL pattern.
     * Useful for waiting for API calls to complete.
     */
    public static void waitForResponse(Page page, String urlPattern) {
        page.waitForResponse(urlPattern, () -> {});
    }
}
