package com.topautomation.mobile.tests.templates;

import com.topautomation.mobile.base.MobileBaseTest;
import com.topautomation.mobile.pages.MobileLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TEMPLATE - MOBILE TEST.   Copy this file, rename it, remove "enabled = false".
 * Needs: Appium server running (or -Dappium.server.autostart=true), a device/emulator, and the app configured
 * (android.app / ios.app). See README.TXT section "MOBILE TESTING".
 *
 * groups = {"mobile", "mobile-..."} -> run with:  mvn test -Pmobile-functional
 */
public class MobileTestTemplate extends MobileBaseTest {

    @Test(enabled = false, groups = {"mobile", "mobile-functional"}, description = "What this test proves")
    public void myFirstMobileTest() {
        MobileLoginPage login = new MobileLoginPage(driver()).openLoginTab();
        login.submit("test@webdriver.io", "Test1234!");
        String alert = login.readAndAcceptAlert();
        Assert.assertTrue(login.isSuccessAlert(alert), "Expected success dialog, got: " + alert);
    }
}
