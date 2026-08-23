package com.automation.base;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Base class for all Page Objects.
 *
 * WHY: Provides reusable convenience methods so Page Objects don't repeat
 * boilerplate. All methods delegate to Playwright's Page/Locator APIs
 * and rely on Playwright's built-in auto-waiting.
 *
 * IMPORTANT:
 *   - These are THIN wrappers, not custom wait frameworks.
 *   - Playwright Locators auto-wait for elements to be actionable.
 *   - DO NOT use Thread.sleep() here.
 *   - Page Objects extend this class and use these helpers.
 */
public abstract class BasePage {

    private static final Logger log = LoggerFactory.getLogger(BasePage.class);

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    // ================================================================
    // ACTIONS — thin wrappers around Playwright Locator
    // ================================================================

    protected void click(Locator locator) {
        locator.click();
        log.debug("Clicked: {}", locator);
    }

    protected void fill(Locator locator, String text) {
        locator.fill(text);
        log.debug("Filled '{}' into: {}", text, locator);
    }

    protected void clearAndFill(Locator locator, String text) {
        locator.clear();
        locator.fill(text);
    }

    protected String getText(Locator locator) {
        return locator.textContent();
    }

    protected String getInputValue(Locator locator) {
        return locator.inputValue();
    }

    // ================================================================
    // STATE — check element state
    // ================================================================

    protected boolean isVisible(Locator locator) {
        return locator.isVisible();
    }

    protected boolean isEnabled(Locator locator) {
        return locator.isEnabled();
    }

    protected boolean isChecked(Locator locator) {
        return locator.isChecked();
    }

    // ================================================================
    // WAITS — explicit waits when auto-waiting isn't enough
    // ================================================================

    /**
     * Waits for a locator to become visible.
     * Use when you need to assert visibility before continuing.
     */
    protected void waitForVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
    }

    /**
     * Waits for a locator to become hidden or detached from DOM.
     */
    protected void waitForHidden(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN));
    }

    /**
     * Waits for the page URL to match the given pattern.
     */
    protected void waitForURL(String urlPattern) {
        page.waitForURL(urlPattern);
    }

    // ================================================================
    // PAGE INFO
    // ================================================================

    protected String getCurrentUrl() {
        return page.url();
    }

    protected String getTitle() {
        return page.title();
    }

    // ================================================================
    // SCREENSHOT — useful for debugging in Page Objects
    // ================================================================

    protected byte[] takeScreenshot() {
        return page.screenshot();
    }

    protected void takeScreenshot(Path path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(path));
    }
}
