package com.automation.assertions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.testng.Assert;

/**
 * Assertion utilities combining Playwright assertions with TestNG.
 *
 * WHY: Playwright's assertThat() provides auto-waiting assertions
 * (e.g. it waits for an element to become visible before checking).
 * This is more reliable than checking state immediately.
 *
 * USAGE:
 *   AssertionUtils.assertVisible(locator);
 *   AssertionUtils.assertUrlContains(page, "dashboard");
 *   AssertionUtils.assertTextEquals(locator, "Welcome");
 */
public class AssertionUtils {

    private AssertionUtils() {}

    // ================================================================
    // PLAYWRIGHT ASSERTIONS — auto-waiting, recommended for UI
    // ================================================================

    /**
     * Asserts that a locator is visible (auto-waits).
     */
    public static void assertVisible(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isVisible();
    }

    /**
     * Asserts that a locator is hidden (auto-waits).
     */
    public static void assertHidden(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isHidden();
    }

    /**
     * Asserts that a locator has the expected text content (auto-waits).
     */
    public static void assertTextEquals(Locator locator, String expected) {
        PlaywrightAssertions.assertThat(locator).hasText(expected);
    }

    /**
     * Asserts that a locator's text contains the expected substring (auto-waits).
     */
    public static void assertTextContains(Locator locator, String expected) {
        PlaywrightAssertions.assertThat(locator).containsText(expected);
    }

    /**
     * Asserts that a locator is enabled.
     */
    public static void assertEnabled(Locator locator) {
        PlaywrightAssertions.assertThat(locator).isEnabled();
    }

    /**
     * Asserts that the page has the expected URL (auto-waits).
     */
    public static void assertUrl(Page page, String expectedUrl) {
        PlaywrightAssertions.assertThat(page).hasURL(expectedUrl);
    }

    /**
     * Asserts that the page URL contains the expected pattern.
     */
    public static void assertUrlContains(Page page, String urlPart) {
        Assert.assertTrue(page.url().contains(urlPart),
                "Expected URL to contain '" + urlPart + "' but was: " + page.url());
    }

    // ================================================================
    // TESTNG ASSERTIONS — for non-UI checks
    // ================================================================

    /**
     * Asserts that a page title matches expected value.
     */
    public static void assertTitle(Page page, String expectedTitle) {
        PlaywrightAssertions.assertThat(page).hasTitle(expectedTitle);
    }

    /**
     * Asserts a boolean condition with a descriptive message.
     */
    public static void assertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);
    }

    /**
     * Asserts two strings are equal with a descriptive message.
     */
    public static void assertEquals(String actual, String expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Asserts two integers are equal.
     */
    public static void assertEquals(int actual, int expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }
}
