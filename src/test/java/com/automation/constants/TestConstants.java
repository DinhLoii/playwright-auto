package com.automation.constants;

/**
 * Reusable test constants — expected messages, titles, etc.
 *
 * WHY: Avoids duplicating string literals across test methods.
 * If OrangeHRM changes an error message text, we update it once here.
 */
public final class TestConstants {

    private TestConstants() {}

    // Page titles
    public static final String DASHBOARD_TITLE = "OrangeHRM";
    public static final String LOGIN_PAGE_TITLE = "OrangeHRM";

    // Error messages
    public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid credentials";
    public static final String REQUIRED_FIELD_MESSAGE = "Required";

    // Dashboard breadcrumb
    public static final String DASHBOARD_BREADCRUMB = "Dashboard";

    // PIM module
    public static final String PIM_MENU_TEXT = "PIM";
    public static final String ADMIN_MENU_TEXT = "Admin";
}
