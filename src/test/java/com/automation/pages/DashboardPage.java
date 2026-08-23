package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.pages.components.Navbar;
import com.automation.pages.components.Sidebar;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for the OrangeHRM Dashboard page.
 *
 * URL: /web/index.php/dashboard/index
 *
 * COMPOSITION:
 *   DashboardPage composes Navbar and Sidebar components.
 *   This avoids duplicating navigation selectors.
 *
 * RULE: Dashboard-specific elements are here.
 * Shared elements (navbar, sidebar) are in component classes.
 */
public class DashboardPage extends BasePage {

    // Composed components — reusable across pages
    private final Navbar navbar;
    private final Sidebar sidebar;

    // Dashboard-specific locators
    private final Locator dashboardHeader;
    private final Locator quickLaunchWidgets;

    public DashboardPage(Page page) {
        super(page);
        this.navbar = new Navbar(page);
        this.sidebar = new Sidebar(page);
        this.dashboardHeader = page.locator(".oxd-topbar-header-breadcrumb");
        this.quickLaunchWidgets = page.locator(".orangehrm-dashboard-grid");
    }

    // ================================================================
    // COMPONENT ACCESS — tests use these to interact with shared UI
    // ================================================================

    public Navbar getNavbar() {
        return navbar;
    }

    public Sidebar getSidebar() {
        return sidebar;
    }

    // ================================================================
    // STATE
    // ================================================================

    /**
     * Checks if the dashboard page is loaded by verifying:
     * 1. URL contains "dashboard"
     * 2. Dashboard header is visible
     */
    public boolean isDashboardDisplayed() {
        return getCurrentUrl().contains("dashboard")
                && isVisible(dashboardHeader);
    }

    /**
     * Returns the dashboard header/breadcrumb text.
     */
    public String getDashboardTitle() {
        return getText(dashboardHeader);
    }

    /**
     * Checks if the quick launch widget section is displayed.
     */
    public boolean isQuickLaunchDisplayed() {
        return isVisible(quickLaunchWidgets);
    }

    /**
     * Returns the page title from the browser tab.
     */
    public String getPageTitle() {
        return getTitle();
    }

    /**
     * Returns the current page URL.
     */
    public String getPageUrl() {
        return getCurrentUrl();
    }
}
