package com.automation.pages.components;

import com.automation.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Sidebar component — the left navigation menu present on authenticated pages.
 *
 * Contains menu items: Admin, PIM, Leave, Time, Recruitment, etc.
 * Reused by: DashboardPage, UserPage, and any authenticated page.
 *
 * WHY a component? The sidebar is identical across pages.
 * Extracting it prevents duplicating navigation selectors.
 */
public class Sidebar extends BasePage {

    private final Locator sidebarMenu;
    private final Locator searchMenuInput;

    public Sidebar(Page page) {
        super(page);
        this.sidebarMenu = page.locator(".oxd-sidepanel");
        this.searchMenuInput = page.locator(".oxd-sidepanel input[placeholder='Search']");
    }

    /**
     * Checks if the sidebar is displayed.
     */
    public boolean isSidebarDisplayed() {
        return isVisible(sidebarMenu);
    }

    /**
     * Navigates to a menu item by its exact text.
     * Example: navigateTo("PIM") clicks the PIM menu item.
     */
    public void navigateTo(String menuItemText) {
        Locator menuItem = page.locator(".oxd-main-menu-item")
                .filter(new Locator.FilterOptions().setHasText(menuItemText));
        click(menuItem);
    }

    /**
     * Searches for a menu item using the sidebar search box.
     */
    public void searchMenu(String query) {
        fill(searchMenuInput, query);
    }

    /**
     * Returns true if a menu item with the given text exists in the sidebar.
     */
    public boolean hasMenuItem(String menuItemText) {
        Locator menuItem = page.locator(".oxd-main-menu-item")
                .filter(new Locator.FilterOptions().setHasText(menuItemText));
        return menuItem.count() > 0;
    }
}
