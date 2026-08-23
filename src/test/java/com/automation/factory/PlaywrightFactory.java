package com.automation.factory;

import com.microsoft.playwright.Playwright;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the Playwright instance lifecycle.
 *
 * WHY: Playwright.create() is expensive — it spawns a Node.js subprocess.
 * We create one instance per suite (not per test) for performance.
 *
 * Thread-safety: Uses ThreadLocal so each parallel thread gets its own instance.
 */
public class PlaywrightFactory {

    private static final Logger log = LoggerFactory.getLogger(PlaywrightFactory.class);

    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();

    private PlaywrightFactory() {
        // Utility class
    }

    /**
     * Creates and stores a Playwright instance for the current thread.
     */
    public static Playwright create() {
        Playwright pw = Playwright.create();
        playwrightThreadLocal.set(pw);
        log.info("Playwright instance created [thread: {}]", Thread.currentThread().getName());
        return pw;
    }

    /**
     * Returns the Playwright instance for the current thread.
     */
    public static Playwright get() {
        return playwrightThreadLocal.get();
    }

    /**
     * Closes and removes the Playwright instance for the current thread.
     */
    public static void close() {
        Playwright pw = playwrightThreadLocal.get();
        if (pw != null) {
            pw.close();
            playwrightThreadLocal.remove();
            log.info("Playwright instance closed [thread: {}]", Thread.currentThread().getName());
        }
    }
}
