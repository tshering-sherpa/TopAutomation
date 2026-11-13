package com.topautomation.web.tests.templates;

import com.topautomation.web.base.BaseTest;
import com.topautomation.web.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TEMPLATE - WEB TEST.   Copy this file, rename the class (and file), remove "enabled = false".
 *
 * 1. extends BaseTest      -> a fresh browser is opened before each test and closed after it. Use driver().
 * 2. groups = {...}        -> decides WHICH Maven profiles run it:
 *                             "web" (always) + any of "web-sanity", "web-functional", "web-regression".
 * 3. Use page objects      -> never call driver.findElement() directly inside a test.
 */
public class WebTestTemplate extends BaseTest {

    @Test(enabled = false, groups = {"web", "web-functional", "web-regression"}, description = "What this test proves")
    public void myFirstWebTest() {
        LoginPage login = new LoginPage(driver()).open();          // 1. open the page
        login.loginAs("tomsmith", "SuperSecretPassword!");          // 2. do something
        Assert.assertTrue(login.isLoggedIn(), "Should be logged in"); // 3. check the result
    }
}
