package com.automation.tests.dashboard;

import com.automation.base.BaseTest;
import com.automation.config.ConfigManager;
import com.automation.constants.TestConstants;
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
 * Dashboard test scenarios — verifies the main landing page after login.
 *
 * All dashboard tests require authentication, so each test logs in first.
 * Phase 14 will show how to optimize this with authentication state reuse.
 */
public class DashboardTest extends BaseTest {

    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndInit() {
        loginPage = new LoginPage(getPage());
        dashboardPage = new DashboardPage(getPage());

        // Login before each test — dashboard requires authentication
        ConfigManager config = ConfigManager.getInstance();
        loginPage.login(config.getUsername(), config.getPassword());
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that the dashboard page loads after login")
    public void testDashboardLoads() {
        // Assert
        Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed after login");
    }

    @Test(groups = {"regression"})
    @Description("Verify the dashboard page title")
    public void testDashboardPageTitle() {
        // Assert
        Assert.assertEquals(dashboardPage.getPageTitle(),
                TestConstants.DASHBOARD_TITLE,
                "Dashboard page title should match");
    }

    @Test(groups = {"regression"})
    @Description("Verify the dashboard URL contains 'dashboard'")
    public void testDashboardUrl() {
        // Assert
        Assert.assertTrue(dashboardPage.getPageUrl().contains("dashboard"),
                "Dashboard URL should contain 'dashboard'");
    }

    @Test(groups = {"smoke"})
    @Description("Verify that the navbar is displayed on the dashboard")
    public void testNavbarDisplayed() {
        // Assert
        Assert.assertTrue(dashboardPage.getNavbar().isNavbarDisplayed(),
                "Navbar should be visible on the dashboard");
    }

    @Test(groups = {"smoke"})
    @Description("Verify that the sidebar is displayed on the dashboard")
    public void testSidebarDisplayed() {
        // Assert
        Assert.assertTrue(dashboardPage.getSidebar().isSidebarDisplayed(),
                "Sidebar should be visible on the dashboard");
    }

    @Test(groups = {"regression"})
    @Description("Verify that the logged-in user name is displayed in the navbar")
    public void testLoggedInUserDisplayed() {
        // Assert
        String userName = dashboardPage.getNavbar().getLoggedInUserName();
        Assert.assertNotNull(userName, "Username in navbar should not be null");
        Assert.assertFalse(userName.isBlank(), "Username in navbar should not be blank");
    }

    @Test(groups = {"regression"})
    @Description("Verify the dashboard breadcrumb text")
    public void testDashboardBreadcrumb() {
        // Assert
        String breadcrumb = dashboardPage.getDashboardTitle();
        Assert.assertTrue(breadcrumb.contains(TestConstants.DASHBOARD_BREADCRUMB),
                "Dashboard breadcrumb should contain '" + TestConstants.DASHBOARD_BREADCRUMB + "'");
    }
}
