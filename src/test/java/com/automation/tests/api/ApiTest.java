package com.automation.tests.api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * API test examples using Playwright's APIRequestContext.
 *
 * WHY Playwright for API testing?
 *   - Same tool for UI + API — no need for RestAssured/HttpClient
 *   - Share authentication cookies between UI and API tests
 *   - Use API calls to set up test data before UI tests
 *
 * This test class does NOT extend BaseTest because API tests
 * don't need a browser. It manages its own Playwright lifecycle.
 *
 * Target: reqres.in — a free REST API designed for testing.
 */
public class ApiTest {

    private static final Logger log = LoggerFactory.getLogger(ApiTest.class);
    private static final String BASE_URL = "https://reqres.in/api";

    private Playwright playwright;
    private APIRequestContext requestContext;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(BASE_URL));
        log.info("API test context created — baseURL: {}", BASE_URL);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (requestContext != null) {
            requestContext.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
        log.info("API test context disposed");
    }

    // ================================================================
    // GET
    // ================================================================

    @Test(groups = {"smoke", "api"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify GET /users returns 200 and a list of users")
    public void testGetUsers() {
        // Act
        APIResponse response = requestContext.get("/users?page=1");

        // Assert
        Assert.assertEquals(response.status(), 200,
                "GET /users should return 200");

        String body = response.text();
        Assert.assertTrue(body.contains("\"data\""),
                "Response should contain 'data' array");
        Assert.assertTrue(body.contains("\"email\""),
                "Response data should contain user emails");

        log.info("GET /users returned {} bytes", body.length());
    }

    @Test(groups = {"regression", "api"})
    @Description("Verify GET /users/{id} returns a single user")
    public void testGetSingleUser() {
        // Act
        APIResponse response = requestContext.get("/users/2");

        // Assert
        Assert.assertEquals(response.status(), 200,
                "GET /users/2 should return 200");

        String body = response.text();
        Assert.assertTrue(body.contains("\"id\":2"),
                "Response should contain user with id 2");
    }

    @Test(groups = {"regression", "api"})
    @Description("Verify GET /users/{id} returns 404 for non-existent user")
    public void testGetNonExistentUser() {
        // Act
        APIResponse response = requestContext.get("/users/999");

        // Assert
        Assert.assertEquals(response.status(), 404,
                "GET /users/999 should return 404");
    }

    // ================================================================
    // POST
    // ================================================================

    @Test(groups = {"regression", "api"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify POST /users creates a new user")
    public void testCreateUser() {
        // Arrange
        String requestBody = """
                {
                    "name": "John Doe",
                    "job": "QA Engineer"
                }
                """;

        // Act
        APIResponse response = requestContext.post("/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(requestBody));

        // Assert
        Assert.assertEquals(response.status(), 201,
                "POST /users should return 201 Created");

        String body = response.text();
        Assert.assertTrue(body.contains("\"name\":\"John Doe\""),
                "Response should contain the created user name");
        Assert.assertTrue(body.contains("\"id\""),
                "Response should contain a generated id");

        log.info("Created user: {}", body);
    }

    // ================================================================
    // PUT
    // ================================================================

    @Test(groups = {"regression", "api"})
    @Description("Verify PUT /users/{id} updates user data")
    public void testUpdateUser() {
        // Arrange
        String requestBody = """
                {
                    "name": "Jane Doe",
                    "job": "Senior QA"
                }
                """;

        // Act
        APIResponse response = requestContext.put("/users/2",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(requestBody));

        // Assert
        Assert.assertEquals(response.status(), 200,
                "PUT /users/2 should return 200");

        String body = response.text();
        Assert.assertTrue(body.contains("\"name\":\"Jane Doe\""),
                "Response should contain updated name");
    }

    // ================================================================
    // DELETE
    // ================================================================

    @Test(groups = {"regression", "api"})
    @Description("Verify DELETE /users/{id} removes a user")
    public void testDeleteUser() {
        // Act
        APIResponse response = requestContext.delete("/users/2");

        // Assert
        Assert.assertEquals(response.status(), 204,
                "DELETE /users/2 should return 204 No Content");
    }
}
