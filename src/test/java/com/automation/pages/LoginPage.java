package com.automation.pages;

import com.automation.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for the OrangeHRM Login page.
 *
 * URL: /web/index.php/auth/login
 *
 * LOCATOR STRATEGY:
 *   - getByPlaceholder for input fields (stable, user-facing text)
 *   - getByRole for the submit button
 *   - CSS for error messages (no better alternative available)
 *
 * RULE: Page Objects perform actions and expose state.
 * They do NOT contain business-level assertions — tests do that.
 */
public class LoginPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

    // Locators
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;
    private final Locator loginTitle;

    public LoginPage(Page page) {
        super(page);
        this.usernameInput = page.getByPlaceholder("Username");
        this.passwordInput = page.getByPlaceholder("Password");
        this.loginButton = page.getByRole(
                com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Login"));
        this.errorMessage = page.locator(".oxd-alert-content--error");
        this.loginTitle = page.locator(".orangehrm-login-title");
    }

    // ================================================================
    // ACTIONS
    // ================================================================

    /**
     * Performs a complete login action.
     *
     * @param username the username to enter
     * @param password the password to enter
     */
    public void login(String username, String password) {
        log.info("Logging in with user: {}", username);
        fill(usernameInput, username);
        fill(passwordInput, password);
        click(loginButton);
    }

    /**
     * Enters the username without submitting.
     */
    public void enterUsername(String username) {
        fill(usernameInput, username);
    }

    /**
     * Enters the password without submitting.
     */
    public void enterPassword(String password) {
        fill(passwordInput, password);
    }

    /**
     * Clicks the Login button without entering credentials.
     */
    public void clickLogin() {
        click(loginButton);
    }

    // ================================================================
    // STATE — expose state for tests to assert
    // ================================================================

    /**
     * Returns the error message text displayed on invalid login.
     */
    public String getErrorMessage() {
        waitForVisible(errorMessage);
        return getText(errorMessage);
    }

    /**
     * Returns true if the login page is currently displayed.
     */
    public boolean isLoginPageDisplayed() {
        return isVisible(loginTitle);
    }

    /**
     * Returns true if an error message is visible.
     */
    public boolean isErrorMessageDisplayed() {
        return isVisible(errorMessage);
    }

    /**
     * Checks if a required-field validation message is shown.
     */
    public boolean isRequiredFieldErrorDisplayed() {
        Locator requiredError = page.locator(".oxd-input-field-error-message");
        return requiredError.count() > 0;
    }
}
