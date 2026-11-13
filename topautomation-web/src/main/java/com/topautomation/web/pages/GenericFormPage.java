package com.topautomation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Map;

/**
 * Locator-driven page object for ANY form, so injection payloads can be thrown at
 * registration, contact, search, checkout... forms without writing a new page object first.
 *
 * <pre>
 *   new GenericFormPage(driver)
 *       .openPath("/contact")
 *       .fill(By.name("email"), payload)
 *       .fill(By.name("message"), payload)
 *       .submit(By.cssSelector("button[type=submit]"));
 * </pre>
 */
public class GenericFormPage extends BasePage {

    public GenericFormPage(WebDriver driver) {
        super(driver);
    }

    public GenericFormPage openPath(String path) {
        open(path);
        return this;
    }

    public GenericFormPage fill(By locator, String value) {
        type(locator, value);
        return this;
    }

    /** Fills every field with the same value (e.g. one attack payload). */
    public GenericFormPage fillAll(Iterable<By> locators, String value) {
        for (By locator : locators) {
            type(locator, value);
        }
        return this;
    }

    public GenericFormPage fillFromMap(Map<By, String> values) {
        values.forEach(this::type);
        return this;
    }

    public GenericFormPage submit(By submitButton) {
        click(submitButton);
        return this;
    }
}
