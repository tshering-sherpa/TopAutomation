package com.topautomation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Page shown after a successful login on the sample app. */
public class SecureAreaPage extends BasePage {

    private final By heading = By.tagName("h2");
    private final By logoutButton = By.cssSelector("a[href='/logout']");

    public SecureAreaPage(WebDriver driver) {
        super(driver);
    }

    public String heading() {
        return textOf(heading);
    }

    public LoginPage logout() {
        click(logoutButton);
        return new LoginPage(driver);
    }
}
