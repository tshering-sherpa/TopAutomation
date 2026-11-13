package com.topautomation.mobile.pages;

import com.topautomation.core.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Shared helpers for mobile page objects (works with AndroidDriver and IOSDriver). */
public abstract class MobileBasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected MobileBasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getInt("explicit.wait.seconds", 10) + 10));
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void tap(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    /** Returns native alert text, or null if no alert appears within a few seconds. Accepts the alert. */
    public String readAndAcceptAlert() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(4)).until(ExpectedConditions.alertIsPresent());
            String text = driver.switchTo().alert().getText();
            driver.switchTo().alert().accept();
            return text;
        } catch (TimeoutException | NoAlertPresentException e) {
            return null;
        }
    }

    public String pageSource() {
        return driver.getPageSource();
    }
}
