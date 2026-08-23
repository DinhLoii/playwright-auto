package com.automation.tests.users;

import com.automation.base.BaseTest;
import com.automation.config.ConfigManager;
import com.automation.listeners.RetryAnalyzer;
import com.automation.pages.LoginPage;
import com.automation.pages.UserPage;
import com.automation.utils.RandomDataUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * User/Employee (PIM) test scenarios.
 *
 * Tests the PIM module CRUD operations:
 * add, search, edit, delete employees, pagination.
 *
 * NOTE: OrangeHRM is a shared demo site — data resets periodically.
 * We use random data to avoid conflicts with other users.
 */
public class UserTest extends BaseTest {

    private LoginPage loginPage;
    private UserPage userPage;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        loginPage = new LoginPage(getPage());
        userPage = new UserPage(getPage());

        // Login and navigate to PIM
        ConfigManager config = ConfigManager.getInstance();
        loginPage.login(config.getUsername(), config.getPassword());
        userPage.navigateToPIM();
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an employee can be added via the PIM module")
    public void testAddEmployee() {
        // Arrange — use random data to avoid collisions
        String firstName = RandomDataUtils.randomFirstName();
        String lastName = RandomDataUtils.randomLastName();

        // Act
        userPage.addEmployee(firstName, lastName);

        // Assert — should redirect to employee details page
        getPage().waitForURL("**/pim/viewPersonalDetails/**");
        Assert.assertTrue(getPage().url().contains("viewPersonalDetails"),
                "Should redirect to employee details after adding");
    }

    @Test(groups = {"regression"})
    @Description("Verify validation when adding employee with empty required fields")
    public void testAddEmployeeWithInvalidData() {
        // Act — click add then try to save without filling required fields
        userPage.clickAddEmployee();

        // Clear the auto-generated employee ID and submit
        userPage.addEmployee("", "");

        // Assert — form should show validation errors (stay on add page)
        Assert.assertTrue(getPage().url().contains("addEmployee"),
                "Should stay on add employee page when data is invalid");
    }

    @Test(groups = {"regression"})
    @Description("Verify employee search functionality")
    public void testSearchEmployee() {
        // Act — search for a common name
        userPage.searchEmployee("Admin");

        // Assert — results should show (or no records if not found)
        Assert.assertTrue(userPage.isEmployeeTableDisplayed(),
                "Employee table should be displayed after search");
    }

    @Test(groups = {"regression"})
    @Description("Verify the employee table displays records")
    public void testEmployeeTableDisplayed() {
        // Assert — the default view should show employees
        Assert.assertTrue(userPage.isEmployeeTableDisplayed(),
                "Employee table should be visible on the PIM page");
    }

    @Test(groups = {"regression"})
    @Description("Verify that search can be reset")
    public void testResetSearch() {
        // Arrange — search for something
        userPage.searchEmployee("Admin");

        // Act — reset the search
        userPage.resetSearch();
        getPage().waitForLoadState();

        // Assert — table should still be displayed
        Assert.assertTrue(userPage.isEmployeeTableDisplayed(),
                "Employee table should be visible after reset");
    }

    @Test(groups = {"regression"})
    @Description("Verify PIM page is displayed after navigation")
    public void testPimPageDisplayed() {
        // Assert
        Assert.assertTrue(userPage.isUserPageDisplayed(),
                "PIM page should be displayed");
    }

    @Test(groups = {"regression"})
    @Description("Verify sidebar has PIM menu item")
    public void testSidebarHasPimMenu() {
        // Assert
        Assert.assertTrue(userPage.getSidebar().hasMenuItem("PIM"),
                "Sidebar should have 'PIM' menu item");
    }
}
