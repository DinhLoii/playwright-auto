package com.automation.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date utility methods for test data generation.
 *
 * WHY: Tests sometimes need formatted dates for form inputs
 * or file names with timestamps.
 */
public class DateUtils {

    private DateUtils() {}

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Returns current timestamp formatted for filenames: 20240315_143022
     */
    public static String getTimestamp() {
        return LocalDateTime.now().format(FILE_TIMESTAMP);
    }

    /**
     * Returns current date formatted for display: 2024-03-15
     */
    public static String getCurrentDate() {
        return LocalDateTime.now().format(DISPLAY_FORMAT);
    }

    /**
     * Returns a formatted date string using a custom pattern.
     */
    public static String formatDate(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
