package com.automation.listeners;

import com.automation.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * TestNG listener for test lifecycle events.
 *
 * WHY: Centrally handles cross-cutting concerns:
 *   - Logging test start/end
 *   - Attaching screenshots to Allure on failure
 *   - Reporting test results
 *
 * HOW: Registered in testng.xml via <listener> element.
 * TestNG calls these methods automatically at each lifecycle event.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("====== Test Suite Started: {} ======", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("====== Test Suite Finished: {} ======", context.getName());
        log.info("Passed: {} | Failed: {} | Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info(">>> Test Started: {}", getTestName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✓ Test Passed: {} ({}ms)",
                getTestName(result), getDuration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("✗ Test Failed: {} — {}",
                getTestName(result),
                result.getThrowable().getMessage());

        // Attach screenshot to Allure report
        attachScreenshotToAllure(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⊘ Test Skipped: {}", getTestName(result));
    }

    // ================================================================
    // HELPERS
    // ================================================================

    private String getTestName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
    }

    private long getDuration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }

    /**
     * Attaches a screenshot to the Allure report for failed tests.
     *
     * We try to get the Page from the test instance (BaseTest) to capture a screenshot.
     * If the page is unavailable, we log a warning instead of failing.
     */
    private void attachScreenshotToAllure(ITestResult result) {
        try {
            Object testInstance = result.getInstance();

            // Use reflection to call getPage() on BaseTest
            var getPageMethod = testInstance.getClass().getMethod("getPage");
            getPageMethod.setAccessible(true);
            var page = (com.microsoft.playwright.Page) getPageMethod.invoke(testInstance);

            if (page != null && !page.isClosed()) {
                byte[] screenshot = ScreenshotUtils.captureScreenshot(page);
                if (screenshot.length > 0) {
                    Allure.addAttachment(
                            "Screenshot on Failure — " + getTestName(result),
                            "image/png",
                            new ByteArrayInputStream(screenshot),
                            ".png");
                    log.debug("Screenshot attached to Allure for: {}", getTestName(result));
                }
            }
        } catch (Exception e) {
            log.warn("Could not attach screenshot to Allure: {}", e.getMessage());
        }
    }
}
