package com.automation.tests.login;

import com.automation.base.BaseTest;
import com.automation.config.ConfigManager;
import com.automation.constants.TestConstants;
import com.automation.constants.URLs;
import com.automation.listeners.RetryAnalyzer;
import com.automation.pages.DashboardPage;
import com.automation.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Login positive test scenarios.
 *
 * Tests follow Arrange → Act → Assert pattern.
 * Page Objects perform actions; Tests verify behavior.
 */
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void initPages() {
        loginPage = new LoginPage(getPage());
        dashboardPage = new DashboardPage(getPage());
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that a user can log in with valid credentials and reach the dashboard")
    public void testSuccessfulLogin() {
        // Arrange
        ConfigManager config = ConfigManager.getInstance();
        String username = config.getUsername();
        String password = config.getPassword();

        // Act
        loginPage.login(username, password);

        // Assert — user should be redirected to dashboard
        Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed after successful login");
        Assert.assertTrue(getPage().url().contains("dashboard"),
                "URL should contain 'dashboard' after login");
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class,
            dependsOnMethods = "testSuccessfulLogin")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a logged-in user can log out successfully")
    public void testLogout() {
        // Arrange — log in first
        ConfigManager config = ConfigManager.getInstance();
        loginPage.login(config.getUsername(), config.getPassword());

        // Act
        dashboardPage.getNavbar().logout();

        // Assert — should be back on login page
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Login page should be displayed after logout");
        Assert.assertTrue(getPage().url().contains("login"),
                "URL should contain 'login' after logout");
    }

    @Test(groups = {"regression"})
    @Description("Verify that the login page title is correct")
    public void testLoginPageTitle() {
        // Assert
        Assert.assertEquals(getPage().title(), TestConstants.LOGIN_PAGE_TITLE,
                "Login page title should be '" + TestConstants.LOGIN_PAGE_TITLE + "'");
    }

    @Test(groups = {"regression"})
    @Description("Verify that the login page URL contains the expected path")
    public void testLoginPageUrl() {
        // Assert
        Assert.assertTrue(getPage().url().contains("login"),
                "Login page URL should contain 'login'");
    }
}
