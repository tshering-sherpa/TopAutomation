package com.topautomation.web.tests;

import com.topautomation.web.base.BaseTest;
import com.topautomation.web.data.WebExcelDataProvider;
import com.topautomation.web.pages.LoginPage;
import com.topautomation.web.pages.SecureAreaPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginTest extends BaseTest {

    @Test(groups = {"web", "web-sanity", "web-functional", "web-regression"},
            description = "A valid user can log in and log out")
    public void validLoginAndLogout() {
        LoginPage login = new LoginPage(driver()).open().loginAs("tomsmith", "SuperSecretPassword!");

        Assert.assertTrue(login.isLoggedIn(), "Expected to land on the secure area");
        Assert.assertTrue(login.flashMessage().contains("You logged into a secure area!"));

        SecureAreaPage secure = login.secureArea();
        Assert.assertEquals(secure.heading(), "Secure Area");
        LoginPage afterLogout = secure.logout();
        Assert.assertTrue(afterLogout.flashMessage().contains("You logged out of the secure area!"));
    }

    @Test(groups = {"web", "web-functional", "web-regression"},
            dataProvider = "loginData", dataProviderClass = WebExcelDataProvider.class,
            description = "Login scenarios driven by the Login sheet in web-testdata.xlsx")
    public void loginDrivenByExcel(Map<String, String> data) {
        LoginPage login = new LoginPage(driver()).open()
                .loginAs(data.get("Username"), data.get("Password"));

        if ("SUCCESS".equalsIgnoreCase(data.get("ExpectedResult"))) {
            Assert.assertTrue(login.isLoggedIn(), data.get("TestCaseId") + ": expected successful login");
        } else {
            Assert.assertFalse(login.isLoggedIn(), data.get("TestCaseId") + ": login must be rejected");
        }
        Assert.assertTrue(login.flashMessage().contains(data.get("ExpectedMessage")),
                data.get("TestCaseId") + ": unexpected message: " + login.flashMessage());
    }
}
