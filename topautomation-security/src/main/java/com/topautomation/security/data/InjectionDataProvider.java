package com.topautomation.security.data;

import com.topautomation.core.config.ConfigManager;
import com.topautomation.security.payloads.InjectionPayloads;
import com.topautomation.security.payloads.PayloadCategory;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * TestNG data providers that feed injection payloads into any test:
 *
 * <pre>
 *   &#64;Test(dataProvider = "webInjection", dataProviderClass = InjectionDataProvider.class)
 *   public void myFormResistsInjection(PayloadCategory category, String payload) { ... }
 * </pre>
 *
 * Narrow the run with -Dpayload.categories=SQLI,XSS and -Dpayload.max.per.category=5
 */
public class InjectionDataProvider {

    @DataProvider(name = "webInjection", parallel = true)
    public static Object[][] web() {
        return build(PayloadCategory.parse(ConfigManager.get("payload.categories"), PayloadCategory.webDefaults()));
    }

    /** Mobile defaults: the mobile-specific family plus the payloads that also apply to mobile text fields. */
    @DataProvider(name = "mobileInjection")
    public static Object[][] mobile() {
        Set<PayloadCategory> defaults = EnumSet.of(PayloadCategory.MOBILE, PayloadCategory.SQLI,
                PayloadCategory.XSS, PayloadCategory.NOSQLI, PayloadCategory.MALFORMED);
        return build(PayloadCategory.parse(ConfigManager.get("payload.categories"), defaults));
    }

    @DataProvider(name = "sqlInjection", parallel = true)
    public static Object[][] sql() {
        return build(Set.of(PayloadCategory.SQLI));
    }

    @DataProvider(name = "xss", parallel = true)
    public static Object[][] xss() {
        return build(Set.of(PayloadCategory.XSS));
    }

    private static Object[][] build(Set<PayloadCategory> categories) {
        int max = ConfigManager.getInt("payload.max.per.category", 0);
        List<Object[]> rows = new ArrayList<>();
        for (PayloadCategory category : categories) {
            List<String> payloads = InjectionPayloads.get(category);
            int limit = max > 0 ? Math.min(max, payloads.size()) : payloads.size();
            for (int i = 0; i < limit; i++) {
                rows.add(new Object[]{category, payloads.get(i)});
            }
        }
        return rows.toArray(new Object[0][]);
    }
}
