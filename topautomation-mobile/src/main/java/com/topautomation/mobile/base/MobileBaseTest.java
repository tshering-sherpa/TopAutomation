package com.topautomation.mobile.base;

import com.topautomation.core.driver.DriverManager;
import com.topautomation.mobile.driver.AppiumServerManager;
import com.topautomation.mobile.driver.MobileDriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/** Base class for all MOBILE tests. Needs a running Appium server + emulator/simulator/device. */
public abstract class MobileBaseTest {

    protected final Logger log = LogManager.getLogger(getClass());

    /** Starts a local Appium server first if appium.server.autostart=true; otherwise expects one to be running. */
    @BeforeClass(alwaysRun = true)
    public void startAppiumServer() {
        AppiumServerManager.startIfEnabled();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverManager.set(MobileDriverFactory.createMobileDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quit();
    }

    protected WebDriver driver() {
        return DriverManager.get();
    }
}
