package com.topautomation.web.base;

import com.topautomation.core.driver.DriverManager;
import com.topautomation.web.driver.WebDriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/** Base class for all WEB tests: one fresh browser per test method (parallel safe). */
public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());

    @Parameters("browser")
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional String browser) {
        DriverManager.set(WebDriverFactory.createWebDriver(browser));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quit();
    }

    protected WebDriver driver() {
        return DriverManager.get();
    }
}
