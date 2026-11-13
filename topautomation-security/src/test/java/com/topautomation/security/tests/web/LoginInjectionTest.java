package com.topautomation.security.tests.web;

import com.topautomation.web.base.BaseTest;
import com.topautomation.security.data.InjectionDataProvider;
import com.topautomation.core.reporting.ExtentTestManager;
import com.topautomation.web.pages.LoginPage;
import com.topautomation.security.payloads.PayloadCategory;
import com.topautomation.security.checks.SecurityAssertions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Throws every payload in the library at the login form and fails if the app:
 * logs the attacker in, runs injected JavaScript, reflects markup unencoded, leaks DB/stack errors,
 * evaluates template expressions or answers a delay payload suspiciously slowly.
 *
 * Run only this:   mvn test -Psecurity-web
 * Narrow it:       mvn test -Psecurity-web -Dpayload.categories=SQLI,XSS -Dpayload.max.per.category=5
 */
public class LoginInjectionTest extends BaseTest {

    @Test(groups = {"security", "security-web"},
            dataProvider = "webInjection", dataProviderClass = InjectionDataProvider.class,
            description = "Login form resists injection payloads")
    public void loginFormResistsInjection(PayloadCategory category, String payload) {
        LoginPage login = new LoginPage(driver()).open();

        long start = System.nanoTime();
        login.loginAs(payload, payload);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        List<String> findings = SecurityAssertions.analyse(driver(), payload, elapsedMs);
        ExtentTestManager.info("Category=" + category + ", response time=" + elapsedMs + " ms, findings=" + findings);

        Assert.assertTrue(findings.isEmpty(), category + " payload produced findings: " + findings);
        Assert.assertFalse(login.isLoggedIn(), category + " payload bypassed authentication!");
    }
}
