package com.topautomation.core.driver;

import org.openqa.selenium.WebDriver;

/** Holds one driver per thread so tests can run in parallel safely. */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver get() {
        return DRIVER.get();
    }

    public static void set(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
