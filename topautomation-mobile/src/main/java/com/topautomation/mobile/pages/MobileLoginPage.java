package com.topautomation.mobile.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * SAMPLE page object. The accessibility ids below match the open-source "wdio native demo app"
 * (https://github.com/webdriverio/native-demo-app). Replace them with your own app's locators.
 * Accessibility ids work on both Android and iOS, which keeps one page object for both platforms.
 */
public class MobileLoginPage extends MobileBasePage {

    private final By loginTab = AppiumBy.accessibilityId("Login");
    private final By emailInput = AppiumBy.accessibilityId("input-email");
    private final By passwordInput = AppiumBy.accessibilityId("input-password");
    private final By loginButton = AppiumBy.accessibilityId("button-LOGIN");

    public MobileLoginPage(WebDriver driver) {
        super(driver);
    }

    public MobileLoginPage openLoginTab() {
        tap(loginTab);
        visible(emailInput);
        return this;
    }

    public MobileLoginPage submit(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        tap(loginButton);
        return this;
    }

    /** In the sample app a successful login shows a native alert titled "Success". */
    public boolean isSuccessAlert(String alertText) {
        return alertText != null && alertText.contains("Success");
    }
}
