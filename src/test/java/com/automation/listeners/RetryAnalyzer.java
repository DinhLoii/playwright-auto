package com.automation.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retries failed tests up to MAX_RETRY times.
 *
 * WHY: Some test failures are caused by transient issues
 * (network glitch, slow page load). Retrying once helps
 * distinguish flaky failures from genuine bugs.
 *
 * IMPORTANT: Keep MAX_RETRY small (1-2). Excessive retries
 * hide real application failures and slow down feedback.
 *
 * USAGE: Apply to individual tests:
 *   @Test(retryAnalyzer = RetryAnalyzer.class)
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);

    private static final int MAX_RETRY = 1;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            log.warn("Retrying test '{}' — attempt {}/{}",
                    result.getMethod().getMethodName(), retryCount, MAX_RETRY);
            return true;
        }
        return false;
    }
}
