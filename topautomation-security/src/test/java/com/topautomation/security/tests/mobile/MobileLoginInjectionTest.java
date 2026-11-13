package com.topautomation.security.tests.mobile;

import com.topautomation.mobile.base.MobileBaseTest;
import com.topautomation.security.data.InjectionDataProvider;
import com.topautomation.mobile.pages.MobileLoginPage;
import com.topautomation.security.payloads.PayloadCategory;
import com.topautomation.security.checks.SecurityAssertions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Feeds mobile-relevant payloads (deep links, intents, SQLite probes, format strings, XSS for WebViews)
 * into the login form. Run: mvn test -Psecurity-mobile  (needs Appium + device/emulator).
 */
public class MobileLoginInjectionTest extends MobileBaseTest {

    @Test(groups = {"security", "security-mobile"},
            dataProvider = "mobileInjection", dataProviderClass = InjectionDataProvider.class,
            description = "Mobile login form resists injection payloads")
    public void loginFormResistsInjection(PayloadCategory category, String payload) {
        MobileLoginPage login = new MobileLoginPage(driver()).openLoginTab();

        long start = System.nanoTime();
        login.submit(payload, payload);
        String alert = login.readAndAcceptAlert();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        // A crashed app would make getPageSource() throw and fail the test, which is the right outcome.
        String content = login.pageSource() + " " + (alert == null ? "" : alert);
        List<String> findings = SecurityAssertions.analyseContent(content, payload, elapsedMs);

        Assert.assertTrue(findings.isEmpty(), category + " payload produced findings: " + findings);
        Assert.assertFalse(login.isSuccessAlert(alert), category + " payload bypassed authentication!");
    }
}
