package com.topautomation.security.tests.api;

import com.topautomation.security.checks.ApiSecurityAssertions;
import com.topautomation.api.BookingFactory;
import com.topautomation.api.endpoints.BookingApi;
import com.topautomation.api.base.ApiBaseTest;
import com.topautomation.core.config.ConfigManager;
import com.topautomation.core.reporting.ExtentTestManager;
import com.topautomation.security.payloads.InjectionPayloads;
import com.topautomation.security.payloads.PayloadCategory;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Fires the shared payload library at every INJECTION POINT of the API: JSON body fields, query string,
 * path parameter and headers. For each response it checks (see ApiSecurityAssertions) for 5xx, leaks,
 * injected headers, etc. - and a per-target "compromised" rule (e.g. login must never hand out a token).
 *
 * Add your own endpoints by registering another Target in the static block below.
 *
 * Run:    mvn test -Psecurity-api
 * Narrow: mvn test -Psecurity-api -Dpayload.categories=SQLI,XSS -Dapi.payload.max.per.category=3
 */
public class ApiInjectionTest extends ApiBaseTest {

    /** One injection point: how to send a payload there, and what response would prove a compromise. */
    private record Target(String name, Function<String, Response> sender, Predicate<Response> compromisedIf) {
    }

    private static final BookingApi API = new BookingApi();
    private static final Map<String, Target> TARGETS = new LinkedHashMap<>();

    static {
        register(new Target("POST /auth [json body: username+password]",
                p -> API.authenticate(p, p),
                r -> r.asString().contains("\"token\"")));
        register(new Target("POST /booking [json body: firstname,lastname,additionalneeds]",
                p -> API.create(BookingFactory.withTextEverywhere(p)),
                r -> false));
        register(new Target("GET /booking [query: firstname]",
                p -> API.search(Map.of("firstname", p)),
                r -> false));
        register(new Target("GET /booking/{id} [path param]",
                API::getByRawId,
                r -> r.statusCode() == 200));
        register(new Target("GET /ping [header: X-Test-Input]",
                p -> {
                    if (!p.matches("[\\x20-\\x7E]*") || p.length() > 4000) {
                        throw new SkipException("Payload cannot be represented in an HTTP header value");
                    }
                    return API.pingWithHeader("X-Test-Input", p);
                },
                r -> false));
    }

    private static void register(Target target) {
        TARGETS.put(target.name(), target);
    }

    @DataProvider(name = "apiInjection", parallel = true)
    public static Object[][] apiInjection() {
        Set<PayloadCategory> categories =
                PayloadCategory.parse(ConfigManager.get("payload.categories"), PayloadCategory.webDefaults());
        int max = ConfigManager.getInt("api.payload.max.per.category", ConfigManager.getInt("payload.max.per.category", 0));

        List<Object[]> rows = new ArrayList<>();
        for (Target target : TARGETS.values()) {
            for (PayloadCategory category : categories) {
                List<String> payloads = InjectionPayloads.get(category);
                int limit = max > 0 ? Math.min(max, payloads.size()) : payloads.size();
                for (int i = 0; i < limit; i++) {
                    rows.add(new Object[]{target.name(), category, payloads.get(i)});
                }
            }
        }
        return rows.toArray(new Object[0][]);
    }

    @Test(groups = {"security", "security-api"}, dataProvider = "apiInjection",
            description = "API injection point resists injection payloads")
    public void apiResistsInjection(String targetName, PayloadCategory category, String payload) {
        Target target = TARGETS.get(targetName);

        long start = System.nanoTime();
        Response response = target.sender().apply(payload);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        List<String> findings = ApiSecurityAssertions.analyse(response, payload, elapsedMs);
        ExtentTestManager.info("Target=" + targetName + ", category=" + category + ", status="
                + response.statusCode() + ", findings=" + findings);

        Assert.assertTrue(findings.isEmpty(), targetName + " <- " + category + " produced findings: " + findings);
        Assert.assertFalse(target.compromisedIf().test(response),
                targetName + " <- " + category + " payload appears to have compromised the endpoint (HTTP "
                        + response.statusCode() + ")");
    }
}
