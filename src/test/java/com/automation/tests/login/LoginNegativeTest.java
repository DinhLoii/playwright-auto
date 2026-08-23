package com.automation.tests.login;

import com.automation.base.BaseTest;
import com.automation.constants.TestConstants;
import com.automation.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Login negative test scenarios — invalid and edge-case inputs.
 *
 * WHY separate from LoginTest?
 * Negative tests are regression-level and can be run independently.
 * Keeping them separate makes the test suite organization cleaner.
 */
public class LoginNegativeTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void initPages() {
        loginPage = new LoginPage(getPage());
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error message when logging in with an invalid username")
    public void testInvalidUsername() {
        // Arrange & Act
        loginPage.login("InvalidUser", "admin123");

        // Assert
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be displayed for invalid username");
        Assert.assertEquals(loginPage.getErrorMessage(),
                TestConstants.INVALID_CREDENTIALS_MESSAGE,
                "Error message text should match expected");
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error message when logging in with an invalid password")
    public void testInvalidPassword() {
        // Arrange & Act
        loginPage.login("Admin", "wrongpassword");

        // Assert
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be displayed for invalid password");
        Assert.assertEquals(loginPage.getErrorMessage(),
                TestConstants.INVALID_CREDENTIALS_MESSAGE,
                "Error message text should match expected");
    }

    @Test(groups = {"regression"})
    @Description("Verify validation when submitting empty username")
    public void testEmptyUsername() {
        // Arrange & Act
        loginPage.enterPassword("admin123");
        loginPage.clickLogin();

        // Assert — should show required field validation
        Assert.assertTrue(loginPage.isRequiredFieldErrorDisplayed(),
                "Required field error should be displayed for empty username");
    }

    @Test(groups = {"regression"})
    @Description("Verify validation when submitting empty password")
    public void testEmptyPassword() {
        // Arrange & Act
        loginPage.enterUsername("Admin");
        loginPage.clickLogin();

        // Assert
        Assert.assertTrue(loginPage.isRequiredFieldErrorDisplayed(),
                "Required field error should be displayed for empty password");
    }

    @Test(groups = {"regression"})
    @Description("Verify validation when submitting both fields empty")
    public void testEmptyUsernameAndPassword() {
        // Act — click login without entering anything
        loginPage.clickLogin();

        // Assert
        Assert.assertTrue(loginPage.isRequiredFieldErrorDisplayed(),
                "Required field error should be displayed for empty credentials");
    }

    @Test(groups = {"regression"})
    @Description("Verify that navigating directly to dashboard without login redirects to login page")
    public void testUnauthorizedAccess() {
        // Act — try to access dashboard directly
        getPage().navigate(getPage().url().replace("login", "dashboard/index"));

        // Assert — should be redirected back to login
        getPage().waitForURL("**/auth/login");
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Unauthorized access should redirect to login page");
    }
}
