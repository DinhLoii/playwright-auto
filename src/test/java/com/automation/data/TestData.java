package com.automation.data;

import com.automation.utils.JsonUtils;

import java.util.List;

/**
 * Facade for loading all test data.
 *
 * WHY: Provides a single entry point for test data access.
 * Tests call TestData.getLoginData() instead of knowing file paths and JSON details.
 *
 * Test data files live in: src/test/resources/testdata/
 */
public class TestData {

    private static final String LOGIN_DATA_FILE = "testdata/login-data.json";
    private static final String USER_DATA_FILE = "testdata/user-data.json";

    private TestData() {
        // Utility class
    }

    /**
     * Loads all login test data scenarios from login-data.json.
     */
    public static List<LoginData> getLoginData() {
        return JsonUtils.readList(LOGIN_DATA_FILE, LoginData.class);
    }

    /**
     * Loads all user/employee test data from user-data.json.
     */
    public static List<UserData> getUserData() {
        return JsonUtils.readList(USER_DATA_FILE, UserData.class);
    }

    /**
     * Returns the first valid login data (expectedResult = "success").
     */
    public static LoginData getValidLoginData() {
        return getLoginData().stream()
                .filter(d -> "success".equals(d.getExpectedResult()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No valid login data found in " + LOGIN_DATA_FILE));
    }

    /**
     * Returns the first user data entry (for quick tests).
     */
    public static UserData getFirstUserData() {
        List<UserData> users = getUserData();
        if (users.isEmpty()) {
            throw new RuntimeException("No user data found in " + USER_DATA_FILE);
        }
        return users.get(0);
    }
}
