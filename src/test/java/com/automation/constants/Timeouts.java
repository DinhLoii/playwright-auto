package com.automation.constants;

/**
 * Timeout constants in milliseconds.
 *
 * WHY: Named timeouts are clearer than magic numbers.
 * "Timeouts.SHORT" is more readable than "5000" scattered in code.
 */
public final class Timeouts {

    private Timeouts() {}

    /** Short timeout for quick operations (5 seconds) */
    public static final int SHORT = 5_000;

    /** Default timeout for standard operations (30 seconds) */
    public static final int DEFAULT = 30_000;

    /** Long timeout for slow operations like file uploads (60 seconds) */
    public static final int LONG = 60_000;

    /** Polling interval for custom waits (500ms) */
    public static final int POLL_INTERVAL = 500;
}
