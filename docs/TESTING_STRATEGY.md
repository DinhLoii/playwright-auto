# Testing Strategy

## Overview

This framework tests **OrangeHRM** — a Human Resource Management application. We test three core modules:

| Module | Type | Priority |
|--------|------|----------|
| Login | Authentication | Critical (Smoke) |
| Dashboard | Landing page verification | High (Smoke + Regression) |
| PIM (Users) | Employee CRUD | High (Regression) |
| API | Backend validation | Normal |

## Test Pyramid

```
        ┌──────────┐
        │  E2E/UI  │  ← This framework (Playwright)
        ├──────────┤
        │   API    │  ← Playwright APIRequestContext
        ├──────────┤
        │   Unit   │  ← Application team (not this framework)
        └──────────┘
```

## Test Groups

| Group | Purpose | When to Run |
|-------|---------|-------------|
| `smoke` | Core happy paths — login, dashboard loads | Every commit, every PR |
| `regression` | Full coverage including edge cases | Nightly, before release |
| `api` | Backend API validation | Every commit (fast) |

## Environment Strategy

| Environment | URL | Purpose |
|-------------|-----|---------|
| `dev` | OrangeHRM Demo | Development testing |
| `staging` | OrangeHRM Demo | Pre-release validation |

## Test Data Strategy

- **JSON files** — `src/test/resources/testdata/` for structured test data
- **Random data** — `RandomDataUtils` for unique values in parallel runs
- **Environment variables** — credentials via `APP_USERNAME` / `APP_PASSWORD`

## Artifact Strategy

| Artifact | When Captured | Storage |
|----------|--------------|---------|
| Screenshot | On failure | `target/screenshots/` |
| Trace | On failure | `target/traces/` |
| Video | When enabled | `target/videos/` |
| Logs | Always | `target/logs/` |
| Allure results | Always | `target/allure-results/` |

## CI/CD Pipeline

```
Push to main/develop
  → GitHub Actions triggered
    → Install Java 21
    → Install Playwright browsers
    → Run smoke tests (headless)
    → Upload Allure results
    → Upload failure artifacts (screenshots, traces, videos)
```

## Retry Strategy

- Max 1 retry for failed tests
- Only catches transient failures (network, timing)
- Genuine bugs are NOT masked by retries
