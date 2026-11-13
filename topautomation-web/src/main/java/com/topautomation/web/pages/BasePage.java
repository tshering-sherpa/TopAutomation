package com.topautomation.web.pages;

import com.topautomation.core.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Common, wait-aware helpers shared by every web page object. */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getInt("explicit.wait.seconds", 10)));
    }

    protected void open(String path) {
        String base = ConfigManager.get("base.url", "");
        driver.get(base.endsWith("/") && path.startsWith("/") ? base + path.substring(1) : base + path);
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /** Clears the field, then types the text (null / empty just clears). */
    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected String textOf(By locator) {
        return visible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(d -> !d.findElements(locator).isEmpty() && d.findElement(locator).isDisplayed());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String title() {
        return driver.getTitle();
    }
}
