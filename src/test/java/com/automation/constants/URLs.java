package com.automation.constants;

/**
 * URL path constants for OrangeHRM.
 *
 * WHY: Centralizes URL paths so they're not scattered across tests.
 * If a URL changes, we update it in one place.
 *
 * NOTE: These are PATHS, not full URLs.
 * The base URL comes from ConfigManager.
 */
public final class URLs {

    private URLs() {}

    public static final String LOGIN_PATH = "/web/index.php/auth/login";
    public static final String DASHBOARD_PATH = "/web/index.php/dashboard/index";
    public static final String PIM_PATH = "/web/index.php/pim/viewEmployeeList";
    public static final String ADD_EMPLOYEE_PATH = "/web/index.php/pim/addEmployee";
    public static final String ADMIN_PATH = "/web/index.php/admin/viewSystemUsers";

    // URL patterns for waitForURL (glob-style)
    public static final String DASHBOARD_PATTERN = "**/dashboard/**";
    public static final String PIM_PATTERN = "**/pim/**";
    public static final String LOGIN_PATTERN = "**/auth/login";
}
