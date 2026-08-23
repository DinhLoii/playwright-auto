# Framework Guide — Learn Playwright Java Automation

A beginner-friendly guide to every concept used in this framework. Each section explains **what** → **why** → **how** → **where in this project** → **code example**.

---

## Table of Contents

1. [What is Playwright?](#1-what-is-playwright)
2. [Why Java + Playwright?](#2-why-java--playwright)
3. [What is Page Object Model?](#3-what-is-page-object-model)
4. [Why BaseTest?](#4-why-basetest)
5. [Why BasePage?](#5-why-basepage)
6. [Why Factory Pattern?](#6-why-factory-pattern)
7. [Browser vs BrowserContext vs Page](#7-browser-vs-browsercontext-vs-page)
8. [Locators](#8-locators)
9. [Auto-Waiting](#9-auto-waiting)
10. [Assertions](#10-assertions)
11. [TestNG Lifecycle](#11-testng-lifecycle)
12. [TestNG Groups](#12-testng-groups)
13. [Parallel Execution](#13-parallel-execution)
14. [Retry on Failure](#14-retry-on-failure)
15. [Test Data Management](#15-test-data-management)
16. [Configuration Management](#16-configuration-management)
17. [API Testing](#17-api-testing)
18. [Authentication State](#18-authentication-state)
19. [Trace Viewer](#19-trace-viewer)
20. [Screenshots](#20-screenshots)
21. [Video Recording](#21-video-recording)
22. [Allure Reporting](#22-allure-reporting)
23. [CI/CD with GitHub Actions](#23-cicd-with-github-actions)

---

## 1. What is Playwright?

**What:** Playwright is a browser automation library created by Microsoft. It can control Chromium, Firefox, and WebKit browsers programmatically.

**Why it exists:** To automate web browsers for testing — clicking buttons, filling forms, verifying content, just like a real user would.

**Key features:**
- Controls 3 browser engines (Chromium, Firefox, WebKit)
- Auto-waits for elements before interacting
- Captures screenshots, videos, and traces
- Works headlessly (no visible browser window)
- Supports API testing alongside UI testing

**Where in this project:** Every test interacts with Playwright through Page Objects and BaseTest.

```java
// Playwright creates and controls the browser
Playwright playwright = Playwright.create();
Browser browser = playwright.chromium().launch();
Page page = browser.newPage();

page.navigate("https://example.com");
page.locator("button").click();  // Playwright auto-waits for the button

playwright.close();
```

---

## 2. Why Java + Playwright?

**What:** Using Java as the programming language with Playwright as the automation library.

**Why:**
- Java is widely used in enterprise testing
- Strong typing catches errors at compile time
- Maven ecosystem provides excellent dependency management
- TestNG is a powerful test framework for Java
- Many QA teams already have Java expertise

**Alternatives:** Playwright also supports JavaScript/TypeScript, Python, and C#. Java is chosen for enterprise environments.

---

## 3. What is Page Object Model?

**What:** A design pattern where each web page has a corresponding Java class containing its locators and actions.

**Why it exists:**
- **Maintainability** — if a locator changes, you update ONE class, not every test
- **Readability** — tests read like user stories, not CSS selectors
- **Reusability** — multiple tests reuse the same page methods

**Where in this project:** `src/test/java/com/automation/pages/`

```java
// WITHOUT Page Object Model (bad — selectors everywhere)
page.locator("[placeholder='Username']").fill("Admin");
page.locator("[placeholder='Password']").fill("admin123");
page.locator("button:has-text('Login')").click();

// WITH Page Object Model (good — clean and readable)
loginPage.login("Admin", "admin123");
```

**Rules:**
- Page Objects **perform actions** and **expose state**
- Page Objects do **NOT** contain test assertions
- Tests use Page Objects and verify results

---

## 4. Why BaseTest?

**What:** An abstract class that all test classes extend. It manages the Playwright lifecycle.

**Why it exists:**
- **DRY** — setup/teardown code is written once
- **Consistency** — every test gets a fresh browser, context, and page
- **Artifact capture** — screenshots and traces are captured automatically on failure

**Where in this project:** `src/test/java/com/automation/base/BaseTest.java`

**How it works:**
```
@BeforeSuite  → Create Playwright
@BeforeMethod → Launch Browser → Create Context → Create Page → Navigate
@Test         → (your test runs here)
@AfterMethod  → Capture artifacts if failed → Close Context → Close Browser
@AfterSuite   → Close Playwright
```

```java
public class LoginTest extends BaseTest {
    @Test
    public void testLogin() {
        // getPage() is provided by BaseTest — no setup needed here
        LoginPage loginPage = new LoginPage(getPage());
        loginPage.login("Admin", "admin123");
    }
}
```

---

## 5. Why BasePage?

**What:** An abstract class that all Page Objects extend. It provides reusable helper methods.

**Why it exists:**
- Thin wrappers around common Playwright operations
- Adds logging to actions
- Reduces boilerplate in Page Objects

**Where in this project:** `src/test/java/com/automation/base/BasePage.java`

```java
public class LoginPage extends BasePage {
    private final Locator usernameInput;

    public LoginPage(Page page) {
        super(page);  // BasePage stores the Page reference
        this.usernameInput = page.getByPlaceholder("Username");
    }

    public void enterUsername(String username) {
        fill(usernameInput, username);  // fill() is from BasePage
    }
}
```

**Important:** BasePage methods are THIN wrappers. They delegate to Playwright — they don't add custom wait logic.

---

## 6. Why Factory Pattern?

**What:** Factory classes create complex objects with proper configuration.

**Why it exists:**
- **Encapsulation** — hides browser creation complexity from tests
- **Configuration** — applies settings (headless, video, trace) in one place
- **Flexibility** — easy to switch browsers without changing tests

**Where in this project:** `src/test/java/com/automation/factory/`

| Factory | Creates | Configuration |
|---------|---------|--------------|
| `PlaywrightFactory` | Playwright instance | ThreadLocal for parallel safety |
| `BrowserFactory` | Browser instance | Browser type, headless mode |
| `PageFactory` | BrowserContext + Page | Video, trace, viewport |

---

## 7. Browser vs BrowserContext vs Page

**What:** Three levels of Playwright's object hierarchy.

```
Playwright (process)
  └── Browser (application)
        └── BrowserContext (incognito session)
              └── Page (tab)
```

**Why three levels:**
- **Browser** — the browser application. Launching is expensive.
- **BrowserContext** — an isolated session with its own cookies, storage, and cache. Like opening an incognito window. Creating is fast.
- **Page** — a single tab. Multiple pages can share one context.

**Where in this project:**
- `BrowserFactory` creates Browser
- `PageFactory` creates BrowserContext and Page
- BaseTest creates a NEW context per test method for isolation

```java
// Each test gets isolated cookies and storage
BrowserContext context = browser.newContext();  // Fresh incognito session
Page page = context.newPage();                  // New tab in that session
```

**Why per-test isolation matters:**
- Test A logs in → cookies saved in context A
- Test B gets its own context B → no cookies from A
- Tests don't interfere with each other

---

## 8. Locators

**What:** Locators identify elements on the page (buttons, inputs, text, etc.).

**Preferred strategy (most stable to least stable):**

| Priority | Method | Example | When to use |
|----------|--------|---------|-------------|
| 1 | `getByRole` | `page.getByRole(BUTTON, name("Login"))` | Best — uses accessibility roles |
| 2 | `getByLabel` | `page.getByLabel("Email")` | For labeled form inputs |
| 3 | `getByPlaceholder` | `page.getByPlaceholder("Username")` | For inputs with placeholder text |
| 4 | `getByText` | `page.getByText("Submit")` | For visible text |
| 5 | `getByTestId` | `page.getByTestId("submit-btn")` | When app has data-testid attributes |
| 6 | CSS | `page.locator(".oxd-button")` | When semantic locators aren't available |
| 7 | XPath | `page.locator("//div[@class='menu']")` | Last resort |

**Where in this project:** Every Page Object defines locators in its constructor.

```java
// GOOD — stable, user-facing locators
this.loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));
this.usernameInput = page.getByPlaceholder("Username");

// AVOID — brittle, breaks with layout changes
this.loginButton = page.locator("//div[3]/form/div[4]/button");
```

---

## 9. Auto-Waiting

**What:** Playwright automatically waits for elements to be ready before interacting with them.

**Why it exists:** Eliminates the need for `Thread.sleep()` or custom wait loops. When you call `locator.click()`, Playwright waits for the element to be:
- Attached to the DOM
- Visible
- Stable (not animating)
- Enabled
- Not obscured by other elements

**Where in this project:** Everywhere! We never use `Thread.sleep()`.

```java
// Playwright waits automatically — no sleep needed
page.getByRole(AriaRole.BUTTON, name("Login")).click();

// DON'T do this:
Thread.sleep(2000);  // BAD — arbitrary wait
page.locator("button").click();
```

---

## 10. Assertions

**What:** Verifying that the application behaves as expected.

**Two types in this framework:**

1. **Playwright Assertions** — auto-waiting, best for UI checks:
```java
// Waits up to timeout for the condition to be true
PlaywrightAssertions.assertThat(locator).isVisible();
PlaywrightAssertions.assertThat(page).hasURL("**/dashboard/**");
```

2. **TestNG Assertions** — immediate check, for non-UI values:
```java
Assert.assertEquals(response.status(), 200, "Should return 200");
Assert.assertTrue(userName.length() > 0, "Username should not be empty");
```

**Where in this project:** `AssertionUtils.java` combines both. Tests use meaningful assertions.

---

## 11. TestNG Lifecycle

**What:** TestNG provides annotations that control when code runs relative to tests.

```
@BeforeSuite    → runs once before ALL tests in the suite
  @BeforeClass  → runs once before all tests in a class
    @BeforeMethod → runs before EACH test method
      @Test      → the actual test
    @AfterMethod  → runs after EACH test method
  @AfterClass   → runs once after all tests in a class
@AfterSuite     → runs once after ALL tests in the suite
```

**Where in this project:**
- `BaseTest.@BeforeSuite` → logs configuration info
- `BaseTest.@BeforeMethod` → creates browser, context, page
- `Test classes.@BeforeMethod` → initializes page objects, logs in if needed
- `BaseTest.@AfterMethod` → captures artifacts, closes browser

---

## 12. TestNG Groups

**What:** Tags that categorize tests for selective execution.

```java
@Test(groups = {"smoke"})       // High-priority, runs on every commit
@Test(groups = {"regression"})  // Full coverage, runs nightly
@Test(groups = {"api"})         // API-only tests
```

**Run specific groups:**
```bash
mvn test -Dgroups=smoke              # Only smoke tests
mvn test -Dgroups=regression         # Only regression tests
mvn test -Dgroups="smoke,regression" # Both groups
```

---

## 13. Parallel Execution

**What:** Running multiple tests simultaneously to reduce total execution time.

**Why ThreadLocal?** Playwright objects (Page, BrowserContext, Browser) are NOT thread-safe. If two threads share the same Page, they'll interfere with each other. `ThreadLocal` gives each thread its own copy.

**Where in this project:** `BaseTest.java` uses `ThreadLocal<Page>`, `ThreadLocal<BrowserContext>`, `ThreadLocal<Browser>`.

```java
// Each thread gets its own page — no interference
private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

protected Page getPage() {
    return pageThreadLocal.get();
}
```

**testng.xml configuration:**
```xml
<suite parallel="tests" thread-count="3">
```

---

## 14. Retry on Failure

**What:** Automatically re-running a failed test before marking it as failed.

**Why:** Some failures are transient (network hiccup, slow server). One retry catches these without masking real bugs.

**Where in this project:** `RetryAnalyzer.java` — max 1 retry.

```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testLogin() { ... }
```

**Important:** Keep retries LOW (1-2). High retry counts hide real bugs.

---

## 15. Test Data Management

**What:** Storing test data in JSON files and loading them into Java objects.

**Why:** Separates test logic from test data. Multiple tests can share the same data. Easy to add new scenarios without changing code.

**Where in this project:**
- Data files: `src/test/resources/testdata/login-data.json`
- POJOs: `LoginData.java`, `UserData.java`
- Loader: `TestData.java` → `JsonUtils.java` → Jackson

```java
// Load all login scenarios from JSON
List<LoginData> scenarios = TestData.getLoginData();

// Get the valid login credentials
LoginData validLogin = TestData.getValidLoginData();
loginPage.login(validLogin.getUsername(), validLogin.getPassword());
```

---

## 16. Configuration Management

**What:** Managing settings (browser, URL, timeout) across environments.

**Priority (highest wins):**
1. System property: `mvn test -Dbrowser=firefox`
2. Environment config: `staging.properties`
3. Default config: `config.properties`

**Where in this project:** `ConfigManager.java`

```java
ConfigManager config = ConfigManager.getInstance();
String baseUrl = config.getBaseUrl();    // reads from properties
boolean headless = config.isHeadless(); // can be overridden via -Dheadless=false
```

---

## 17. API Testing

**What:** Testing backend REST APIs without a browser.

**Why with Playwright?**
- Same tool for UI + API
- Can share authentication between UI and API tests
- Use API to set up test data before UI tests

**Where in this project:** `ApiTest.java` using `Playwright.request().newContext()`

```java
APIRequestContext api = playwright.request().newContext(
    new APIRequest.NewContextOptions().setBaseURL("https://reqres.in/api"));

APIResponse response = api.get("/users/2");
Assert.assertEquals(response.status(), 200);
```

---

## 18. Authentication State

**What:** Saving the browser's login session (cookies, localStorage) and reusing it across tests.

**Why:** Logging in via UI for every test is slow. Save the state once, reuse it for subsequent tests.

**How:**
```java
// Save state after logging in
context.storageState(new BrowserContext.StorageStateOptions()
    .setPath(Paths.get("auth/auth-state.json")));

// Reuse state in a new context (skips login)
BrowserContext context = browser.newContext(
    new Browser.NewContextOptions()
        .setStorageStatePath(Paths.get("auth/auth-state.json")));
```

**When to use fresh login instead:**
- Testing the login flow itself
- Testing session expiry
- Tests that modify user permissions
- When storage state might be stale

**Important:** Never commit `auth/auth-state.json` — it contains session tokens.

---

## 19. Trace Viewer

**What:** A recording of everything that happened during a test — DOM snapshots, network calls, console logs.

**Why:** The best debugging tool for failed tests. Shows exactly what the page looked like at each step.

**Where in this project:** `PageFactory.java` starts tracing; `TestHooks.java` saves it on failure.

**View a trace:**
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace target/traces/testName.zip"
```
Or upload to [trace.playwright.dev](https://trace.playwright.dev/).

---

## 20. Screenshots

**What:** Capturing the browser viewport as an image.

**When captured:** Automatically on test failure (via `TestHooks.captureScreenshot`).

**Where saved:** `target/screenshots/`

```java
// Automatic (in TestHooks)
page.screenshot(new Page.ScreenshotOptions()
    .setPath(screenshotPath)
    .setFullPage(true));

// Manual (in a test)
ScreenshotUtils.captureAndSave(page, "my-screenshot.png");
```

---

## 21. Video Recording

**What:** Recording the browser session as a video file.

**When enabled:** Set `video=true` in config or `-Dvideo=true`.

**Where saved:** `target/videos/`

```java
// Configured in PageFactory when creating the context
BrowserContext context = browser.newContext(new Browser.NewContextOptions()
    .setRecordVideoDir(Paths.get("target/videos/")));
```

**Important:** Video is saved when `context.close()` is called.

---

## 22. Allure Reporting

**What:** A beautiful test report showing results, screenshots, and execution details.

**Where in this project:**
- `allure.properties` — configures results directory
- `TestListener.java` — attaches screenshots to failed tests
- Test annotations — `@Severity`, `@Description`

**Generate report:**
```bash
mvn allure:serve  # Opens report in browser
```

---

## 23. CI/CD with GitHub Actions

**What:** Automatically running tests when code is pushed.

**Where in this project:** `.github/workflows/playwright-tests.yml`

**Pipeline flow:**
```
Push code → GitHub Actions triggers → Java 21 setup → Install browsers
→ Run smoke tests → Upload Allure results → Upload failure artifacts
```

**Secrets:** Set `APP_USERNAME` and `APP_PASSWORD` in GitHub repository settings.
