package com.automation.base;

import com.automation.config.ConfigManager;
import com.automation.factory.BrowserFactory;
import com.automation.factory.PageFactory;
import com.automation.factory.PlaywrightFactory;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * Base class for all UI test classes.
 *
 * WHY: Manages the Playwright lifecycle so test classes focus on test logic.
 *
 * LIFECYCLE (per test method):
 *   @BeforeSuite  → Create Playwright instance
 *   @BeforeMethod → Launch Browser → Create Context → Create Page → Navigate
 *   @Test         → (subclass runs the actual test)
 *   @AfterMethod  → Capture artifacts if failed → Close Context → Close Browser
 *   @AfterSuite   → Close Playwright instance
 *
 * PARALLEL SAFETY:
 *   Each thread gets its own Playwright/Browser/Context/Page via ThreadLocal.
 *   Tests NEVER share Page or BrowserContext objects.
 */
public abstract class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    // ThreadLocal ensures each parallel test thread has its own browser objects
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    /**
     * Returns the Page for the current test thread.
     * Subclasses use this to pass to Page Objects.
     */
    protected Page getPage() {
        return pageThreadLocal.get();
    }

    protected BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    // ================================================================
    // SUITE LIFECYCLE
    // ================================================================

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        log.info("========== SUITE START ==========");
        log.info("Environment: {}", ConfigManager.getInstance().getEnvironment().getName());
        log.info("Browser:     {}", ConfigManager.getInstance().getBrowser().getName());
        log.info("Headless:    {}", ConfigManager.getInstance().isHeadless());
        log.info("Base URL:    {}", ConfigManager.getInstance().getBaseUrl());
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        log.info("========== SUITE END ==========");
    }

    // ================================================================
    // TEST METHOD LIFECYCLE
    // ================================================================

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ConfigManager config = ConfigManager.getInstance();

        // 1. Create Playwright (if not already created for this thread)
        Playwright playwright = PlaywrightFactory.get();
        if (playwright == null) {
            playwright = PlaywrightFactory.create();
        }

        // 2. Launch browser
        Browser browser = BrowserFactory.createBrowser(playwright);
        browserThreadLocal.set(browser);

        // 3. Create isolated context (with video/trace settings)
        BrowserContext context = PageFactory.createContext(browser);
        contextThreadLocal.set(context);

        // 4. Create a new page (tab)
        Page page = PageFactory.createPage(context);
        pageThreadLocal.set(page);

        // 5. Set default timeout
        page.setDefaultTimeout(config.getTimeout());

        // 6. Navigate to base URL
        page.navigate(config.getBaseUrl());

        log.info("Test setup complete — navigated to {}", config.getBaseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        Page page = pageThreadLocal.get();
        BrowserContext context = contextThreadLocal.get();
        Browser browser = browserThreadLocal.get();

        String testName = result.getMethod().getMethodName();

        // Capture artifacts on failure
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("TEST FAILED: {}", testName);

            if (ConfigManager.getInstance().isScreenshotOnFailure()) {
                TestHooks.captureScreenshot(page, testName);
            }
            TestHooks.captureTrace(context, testName);
        } else {
            log.info("TEST PASSED: {}", testName);
            // Stop tracing without saving for passed tests
            stopTracingQuietly(context);
        }

        // Close in reverse order: Page → Context → Browser
        closeSafely(page, "Page");
        closeSafely(context, "Context");
        closeSafely(browser, "Browser");

        // Clean up ThreadLocal references
        pageThreadLocal.remove();
        contextThreadLocal.remove();
        browserThreadLocal.remove();

        // Close Playwright for this thread
        PlaywrightFactory.close();
    }

    // ================================================================
    // HELPERS
    // ================================================================

    private void stopTracingQuietly(BrowserContext context) {
        if (context != null && ConfigManager.getInstance().isTraceEnabled()) {
            try {
                context.tracing().stop();
            } catch (Exception e) {
                // Tracing may not have been started — that's OK
            }
        }
    }

    private void closeSafely(AutoCloseable resource, String name) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                log.warn("Error closing {}: {}", name, e.getMessage());
            }
        }
    }
}
