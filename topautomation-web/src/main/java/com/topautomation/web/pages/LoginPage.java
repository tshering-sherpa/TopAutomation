package com.topautomation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the sample login form (https://the-internet.herokuapp.com/login).
 * Replace the locators / URL with your own application's login page.
 */
public class LoginPage extends BasePage {

    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By flashMessage = By.id("flash");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        open("/login");
        visible(usernameInput);
        return this;
    }

    /** Fills and submits the form, then waits briefly for the server response to render. */
    public LoginPage loginAs(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
        try {
            visible(flashMessage);
        } catch (UnhandledAlertException | TimeoutException ignored) {
            // A JS dialog (possible XSS) or slow page: callers inspect the final state themselves.
        }
        return this;
    }

    public String flashMessage() {
        return textOf(flashMessage);
    }

    public boolean isLoggedIn() {
        return currentUrl().contains("/secure");
    }

    public SecureAreaPage secureArea() {
        return new SecureAreaPage(driver);
    }
}
