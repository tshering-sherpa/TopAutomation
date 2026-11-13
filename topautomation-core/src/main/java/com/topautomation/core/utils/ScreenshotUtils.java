package com.topautomation.core.utils;

import com.topautomation.core.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public final class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    /** Base64 PNG of the current thread's driver, or null if unavailable (e.g. driver already closed). */
    public static String base64() {
        WebDriver driver = DriverManager.get();
        if (!(driver instanceof TakesScreenshot shooter)) {
            return null;
        }
        try {
            return shooter.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            return null;
        }
    }
}
