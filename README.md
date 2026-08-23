# Playwright Java Automation Framework

A production-style test automation framework using **Playwright Java**, **TestNG**, and **Allure** with Page Object Model architecture. Built for real-world use and as a learning resource.

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Maven | 3.9+ | Build & dependency management |
| Playwright | 1.52.0 | Browser automation |
| TestNG | 7.10.2 | Test framework |
| Allure | 2.29.1 | Reporting |
| Jackson | 2.18.3 | JSON test data |
| SLF4J + Logback | 2.0.16 | Logging |

## Architecture

```
Test Class → BaseTest → PlaywrightFactory → Playwright
                     → BrowserFactory    → Browser
                     → PageFactory       → BrowserContext → Page

Test Class → Page Object → BasePage → Page (Playwright)
                        → Component Objects (Navbar, Sidebar)

Test Class → TestData → JsonUtils → JSON files
```

**Key principles:**
- **Page Object Model** — each page has a class with locators and actions
- **Composition** — components (Navbar, Sidebar) are reused across pages
- **ThreadLocal** — each parallel test thread gets its own browser objects
- **Factory Pattern** — PlaywrightFactory, BrowserFactory, PageFactory manage lifecycle

## Project Structure

```
├── pom.xml                           # Maven config
├── testng.xml                        # TestNG suite definition
├── .github/workflows/                # CI/CD
│   └── playwright-tests.yml
├── docs/
│   ├── FRAMEWORK_GUIDE.md            # Learning guide
│   └── TESTING_STRATEGY.md           # Test strategy
└── src/test/
    ├── java/com/automation/
    │   ├── base/                     # BaseTest, BasePage, TestHooks
    │   ├── config/                   # ConfigReader, ConfigManager
    │   ├── factory/                  # Playwright/Browser/Page factories
    │   ├── pages/                    # Page Objects (Login, Dashboard, User)
    │   │   └── components/           # Reusable components (Navbar, Sidebar)
    │   ├── tests/                    # Test classes by feature
    │   │   ├── login/
    │   │   ├── dashboard/
    │   │   ├── users/
    │   │   └── api/
    │   ├── data/                     # Test data POJOs
    │   ├── constants/                # URLs, Timeouts, TestConstants
    │   ├── utils/                    # Utilities
    │   ├── listeners/                # TestNG listeners
    │   ├── assertions/               # Assertion helpers
    │   └── enums/                    # BrowserType, Environment
    └── resources/
        ├── config/                   # Properties files per environment
        ├── testdata/                 # JSON test data
        ├── schemas/                  # JSON schemas
        ├── logback-test.xml          # Logging config
        └── allure.properties         # Allure config
```

## Installation

### Prerequisites
- Java 21 (JDK)
- Maven 3.9+
- Git

### Setup
```bash
# Clone the repository
git clone <your-repo-url>
cd playwright-java-automation

# Install Playwright browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"

# Verify compilation
mvn clean compile test-compile
```

### Environment Variables (optional)
```bash
cp .env.example .env
# Edit .env with your credentials
```

## Running Tests

### Run all tests
```bash
mvn clean test
```

### Run smoke tests
```bash
mvn test -Dgroups=smoke
```

### Run regression tests
```bash
mvn test -Dgroups=regression
```

### Run API tests only
```bash
mvn test -Dgroups=api
```

### Run a specific test class
```bash
mvn test -Dtest=LoginTest
```

### Run a specific test method
```bash
mvn test -Dtest=LoginTest#testSuccessfulLogin
```

## Browser Options

### Run with Firefox
```bash
mvn test -Dbrowser=firefox
```

### Run with WebKit (Safari engine)
```bash
mvn test -Dbrowser=webkit
```

### Run in headed mode (visible browser)
```bash
mvn test -Dheadless=false
```

### Combine options
```bash
mvn test -Dgroups=smoke -Dbrowser=firefox -Dheadless=false
```

## Environment Configuration

### Run against staging
```bash
mvn test -Denv=staging
```

### Run against dev (default)
```bash
mvn test -Denv=dev
```

**Configuration priority:**
1. System property (`-Dbrowser=firefox`)
2. Environment config (`staging.properties`)
3. Default config (`config.properties`)

## Parallel Execution

TestNG is configured for parallel execution in `testng.xml`:

```xml
<suite parallel="tests" thread-count="3">
```

Each thread gets isolated Playwright/Browser/Context/Page via `ThreadLocal`.

## Allure Report

### Generate and view report
```bash
# After running tests
mvn allure:serve
```

### Generate report files only
```bash
mvn allure:report
# Report at: target/site/allure-maven/index.html
```

### What's in the report
- Test results with pass/fail/skip status
- Screenshots on failure (auto-attached)
- Test severity levels
- Test descriptions
- Execution time

## Debugging Failures

### Screenshots
Failed tests automatically save screenshots to `target/screenshots/`.

### Traces
Failed tests save Playwright traces to `target/traces/`.

View traces with:
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace target/traces/your-trace.zip"
```

Or upload to [trace.playwright.dev](https://trace.playwright.dev/).

### Videos
Enable video recording:
```bash
mvn test -Dvideo=true
```
Videos saved to `target/videos/`.

### Logs
Test execution logs: `target/logs/test-execution.log`

## CI/CD

GitHub Actions workflow (`.github/workflows/playwright-tests.yml`):
1. Checkout code
2. Setup Java 21
3. Install Playwright browsers
4. Run smoke tests
5. Upload artifacts (Allure results, screenshots, traces, videos)

### GitHub Secrets required
| Secret | Description |
|---|---|
| `APP_USERNAME` | Application login username |
| `APP_PASSWORD` | Application login password |

## Git Workflow

Use conventional commits:
```
feat: add login page object
test: add invalid login scenarios
fix: handle dashboard loading issue
refactor: improve browser lifecycle management
docs: update framework guide
ci: add playwright github actions workflow
chore: update dependencies
```

## Best Practices

1. **Locator strategy**: Prefer `getByRole` > `getByLabel` > `getByPlaceholder` > `getByText` > `getByTestId` > CSS > XPath
2. **No Thread.sleep()**: Use Playwright's built-in auto-waiting
3. **Page Objects don't assert**: They perform actions and expose state
4. **Tests follow AAA**: Arrange → Act → Assert
5. **Random test data**: Use `RandomDataUtils` to avoid collisions
6. **Never commit secrets**: Use environment variables or GitHub Secrets
7. **Keep retries small**: Max 1 retry to catch flakiness without hiding bugs

## Future Improvements

- [ ] Data-driven tests with TestNG DataProvider
- [ ] Visual regression testing with screenshot comparison
- [ ] Docker containerization for consistent test environments
- [ ] Multi-browser parallel matrix in CI
- [ ] Slack/Teams notification on failure
- [ ] Test execution dashboard
- [ ] Performance metrics collection
- [ ] Custom Allure categories and environment info
- [ ] Database validation utilities
- [ ] Mock server for API contract testing
