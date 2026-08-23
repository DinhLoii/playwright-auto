package com.automation.pages.components;

import com.automation.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Navbar component — the top header bar present on most pages after login.
 *
 * Contains: user dropdown, logout, search bar.
 * Reused by: DashboardPage, UserPage, and any page that shows the navbar.
 *
 * WHY a component? The navbar appears on many pages. Extracting it avoids
 * duplicating selectors and actions across multiple Page Objects.
 */
public class Navbar extends BasePage {

    // Locators — using stable selectors from OrangeHRM
    private final Locator userDropdown;
    private final Locator logoutLink;
    private final Locator searchInput;
    private final Locator userNameText;

    public Navbar(Page page) {
        super(page);
        this.userDropdown = page.locator(".oxd-userdropdown");
        this.logoutLink = page.getByRole(com.microsoft.playwright.options.AriaRole.MENUITEM,
                new Page.GetByRoleOptions().setName("Logout"));
        this.searchInput = page.locator("input[placeholder='Search']");
        this.userNameText = page.locator(".oxd-userdropdown-name");
    }

    /**
     * Clicks the user dropdown to reveal the logout menu.
     */
    public void openUserMenu() {
        click(userDropdown);
    }

    /**
     * Logs the user out by clicking the dropdown then the Logout link.
     */
    public void logout() {
        openUserMenu();
        click(logoutLink);
    }

    /**
     * Returns the displayed username text (e.g. "Admin").
     */
    public String getLoggedInUserName() {
        return getText(userNameText);
    }

    /**
     * Checks if the navbar user dropdown is visible (indicates logged-in state).
     */
    public boolean isNavbarDisplayed() {
        return isVisible(userDropdown);
    }

    /**
     * Types a search query into the navbar search box.
     */
    public void search(String query) {
        fill(searchInput, query);
    }
}
