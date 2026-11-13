package com.topautomation.mobile.tests;

import com.topautomation.mobile.base.MobileBaseTest;
import com.topautomation.mobile.data.MobileExcelDataProvider;
import com.topautomation.mobile.pages.MobileLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/** Requires a running Appium server and the sample app configured in mobile.properties. */
public class MobileLoginTest extends MobileBaseTest {

    @Test(groups = {"mobile", "mobile-sanity", "mobile-functional", "mobile-regression"},
            description = "Valid credentials show the success dialog")
    public void validLoginShowsSuccess() {
        MobileLoginPage login = new MobileLoginPage(driver()).openLoginTab();
        login.submit("test@webdriver.io", "Test1234!");
        String alert = login.readAndAcceptAlert();
        Assert.assertTrue(login.isSuccessAlert(alert), "Expected success alert but got: " + alert);
    }

    @Test(groups = {"mobile", "mobile-functional", "mobile-regression"},
            description = "Invalid credentials do not show the success dialog")
    public void invalidLoginIsRejected() {
        MobileLoginPage login = new MobileLoginPage(driver()).openLoginTab();
        login.submit("not-an-email", "x");
        String alert = login.readAndAcceptAlert();
        Assert.assertFalse(login.isSuccessAlert(alert), "Invalid login must not succeed");
    }

    @Test(groups = {"mobile", "mobile-functional", "mobile-regression"},
            dataProvider = "mobileLoginData", dataProviderClass = MobileExcelDataProvider.class,
            description = "Login scenarios driven by the MobileLogin sheet in mobile-testdata.xlsx")
    public void loginDrivenByExcel(Map<String, String> data) {
        MobileLoginPage login = new MobileLoginPage(driver()).openLoginTab();
        login.submit(data.get("Email"), data.get("Password"));
        String alert = login.readAndAcceptAlert();
        boolean expectSuccess = "SUCCESS".equalsIgnoreCase(data.get("ExpectedResult"));
        Assert.assertEquals(login.isSuccessAlert(alert), expectSuccess,
                data.get("TestCaseId") + ": unexpected result, alert was: " + alert);
    }
}
