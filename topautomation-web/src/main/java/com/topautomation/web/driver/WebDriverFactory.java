package com.topautomation.web.driver;

import com.topautomation.core.config.ConfigManager;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

/**
 * Creates Selenium web drivers (chrome | firefox | edge, local or Grid).
 * Selenium Manager (bundled with Selenium) downloads matching browser drivers automatically.
 */
public final class WebDriverFactory {

    private WebDriverFactory() {
    }

    // ------------------------------------------------------------------ WEB

    public static WebDriver createWebDriver(String browserOverride) {
        String browser = (browserOverride == null || browserOverride.isBlank())
                ? ConfigManager.get("browser", "chrome")
                : browserOverride;
        browser = browser.toLowerCase();
        boolean headless = ConfigManager.getBoolean("headless", false);
        boolean ci = ConfigManager.getBoolean("ci", false);
        String grid = ConfigManager.get("grid.url", "");

        WebDriver driver;
        switch (browser) {
            case "chrome" -> {
                ChromeOptions o = chromeOptions(headless, ci);
                driver = grid.isBlank() ? new ChromeDriver(o) : new RemoteWebDriver(toUrl(grid), o);
            }
            case "firefox" -> {
                FirefoxOptions o = new FirefoxOptions();
                if (headless) {
                    o.addArguments("-headless");
                }
                o.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
                driver = grid.isBlank() ? new FirefoxDriver(o) : new RemoteWebDriver(toUrl(grid), o);
            }
            case "edge" -> {
                EdgeOptions o = new EdgeOptions();
                if (headless) {
                    o.addArguments("--headless=new", "--window-size=1920,1080");
                }
                if (ci) {
                    o.addArguments("--no-sandbox", "--disable-dev-shm-usage");
                }
                o.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
                driver = grid.isBlank() ? new EdgeDriver(o) : new RemoteWebDriver(toUrl(grid), o);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "'. Use chrome | firefox | edge");
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigManager.getInt("implicit.wait.seconds", 0)));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigManager.getInt("page.load.timeout.seconds", 30)));
        if (!headless) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    private static ChromeOptions chromeOptions(boolean headless, boolean ci) {
        ChromeOptions o = new ChromeOptions();
        if (headless) {
            o.addArguments("--headless=new", "--window-size=1920,1080");
        }
        if (ci) {
            o.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        }
        o.addArguments("--disable-notifications", "--remote-allow-origins=*");
        // Stop Chrome's password-manager / breach popups from interfering with login tests
        o.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        // IGNORE keeps JS alert()s open so security tests can detect XSS execution
        o.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
        return o;
    }

    private static URL toUrl(String url) {
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
}
