package com.automation.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for login test data.
 * Mapped from login-data.json using Jackson.
 *
 * Each instance represents one login test scenario
 * (valid login, invalid username, empty password, etc.)
 */
public class LoginData {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("expectedResult")
    private String expectedResult;

    @JsonProperty("description")
    private String description;

    // Default constructor required by Jackson
    public LoginData() {}

    public LoginData(String username, String password, String expectedResult, String description) {
        this.username = username;
        this.password = password;
        this.expectedResult = expectedResult;
        this.description = description;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "LoginData{username='" + username + "', description='" + description + "'}";
    }
}
