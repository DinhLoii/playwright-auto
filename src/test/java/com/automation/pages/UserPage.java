package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.pages.components.Navbar;
import com.automation.pages.components.Sidebar;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for the OrangeHRM PIM (Personnel Information Management) module.
 *
 * URL: /web/index.php/pim/viewEmployeeList
 *
 * This module handles employee CRUD operations:
 * add, search, edit, delete employees.
 *
 * WHY "UserPage" not "PimPage"?
 * The requirement specified UserPage. In OrangeHRM, the PIM module
 * is the closest equivalent to a "users" management page.
 */
public class UserPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(UserPage.class);

    private final Navbar navbar;
    private final Sidebar sidebar;

    // PIM employee list locators
    private final Locator addEmployeeButton;
    private final Locator employeeNameSearchInput;
    private final Locator searchButton;
    private final Locator resetButton;
    private final Locator employeeTable;
    private final Locator tableRows;
    private final Locator noRecordsMessage;

    // Add employee form locators
    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator employeeIdInput;
    private final Locator saveButton;

    // Pagination
    private final Locator paginationNext;
    private final Locator paginationPrev;
    private final Locator paginationInfo;

    public UserPage(Page page) {
        super(page);
        this.navbar = new Navbar(page);
        this.sidebar = new Sidebar(page);

        // Employee list
        this.addEmployeeButton = page.getByRole(
                com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(" Add"));
        this.employeeNameSearchInput = page.locator(
                ".oxd-form .oxd-input").first();
        this.searchButton = page.getByRole(
                com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Search"));
        this.resetButton = page.getByRole(
                com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Reset"));
        this.employeeTable = page.locator(".oxd-table");
        this.tableRows = page.locator(".oxd-table-body .oxd-table-row");
        this.noRecordsMessage = page.locator(".orangehrm-horizontal-padding .oxd-text");

        // Add employee form
        this.firstNameInput = page.getByPlaceholder("First Name");
        this.lastNameInput = page.getByPlaceholder("Last Name");
        this.employeeIdInput = page.locator(".orangehrm-employee-form .oxd-input").nth(0);
        this.saveButton = page.getByRole(
                com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Save"));

        // Pagination
        this.paginationNext = page.locator(".oxd-pagination__ul .oxd-pagination-page-item--next");
        this.paginationPrev = page.locator(".oxd-pagination__ul .oxd-pagination-page-item--previous");
        this.paginationInfo = page.locator(".orangehrm-pagination");
    }

    // ================================================================
    // NAVIGATION
    // ================================================================

    /**
     * Navigates to the PIM Employee List page via the sidebar.
     */
    public void navigateToPIM() {
        sidebar.navigateTo("PIM");
        page.waitForURL("**/pim/viewEmployeeList");
        log.info("Navigated to PIM Employee List");
    }

    // ================================================================
    // ACTIONS
    // ================================================================

    /**
     * Clicks the "Add" button to open the Add Employee form.
     */
    public void clickAddEmployee() {
        click(addEmployeeButton);
        page.waitForURL("**/pim/addEmployee");
    }

    /**
     * Fills in and submits the Add Employee form.
     */
    public void addEmployee(String firstName, String lastName) {
        clickAddEmployee();
        fill(firstNameInput, firstName);
        fill(lastNameInput, lastName);
        click(saveButton);
        log.info("Adding employee: {} {}", firstName, lastName);
    }

    /**
     * Searches for an employee by name.
     */
    public void searchEmployee(String employeeName) {
        fill(employeeNameSearchInput, employeeName);
        click(searchButton);
        // Wait for table to refresh
        page.waitForLoadState();
        log.info("Searched for employee: {}", employeeName);
    }

    /**
     * Resets the search filters.
     */
    public void resetSearch() {
        click(resetButton);
    }

    /**
     * Clicks the edit icon on the first row in the employee table.
     */
    public void editFirstEmployee() {
        Locator editButton = tableRows.first().locator(".oxd-icon.bi-pencil-fill");
        click(editButton);
    }

    /**
     * Clicks the delete icon on the first row, then confirms deletion.
     */
    public void deleteFirstEmployee() {
        Locator deleteButton = tableRows.first().locator(".oxd-icon.bi-trash");
        click(deleteButton);

        // Confirm deletion in the dialog
        Locator confirmDelete = page.getByRole(
                com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Yes, Delete"));
        click(confirmDelete);
        log.info("Deleted first employee from list");
    }

    // ================================================================
    // PAGINATION
    // ================================================================

    public void goToNextPage() {
        click(paginationNext);
    }

    public void goToPreviousPage() {
        click(paginationPrev);
    }

    public boolean isPaginationDisplayed() {
        return paginationInfo.count() > 0;
    }

    // ================================================================
    // STATE
    // ================================================================

    public boolean isEmployeeTableDisplayed() {
        return isVisible(employeeTable);
    }

    public int getEmployeeRowCount() {
        return tableRows.count();
    }

    public boolean isNoRecordsDisplayed() {
        return noRecordsMessage.isVisible() &&
                getText(noRecordsMessage).contains("No Records Found");
    }

    /**
     * Returns true if the PIM page is displayed.
     */
    public boolean isUserPageDisplayed() {
        return getCurrentUrl().contains("pim");
    }

    public Navbar getNavbar() {
        return navbar;
    }

    public Sidebar getSidebar() {
        return sidebar;
    }
}
