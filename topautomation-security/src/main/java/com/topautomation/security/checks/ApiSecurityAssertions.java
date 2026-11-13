package com.topautomation.security.checks;

import com.topautomation.security.checks.SecurityAssertions;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * API flavour of the injection checks. Empty list = nothing suspicious.
 * Flags: 5xx (unhandled input), DB/stack-trace/file/command leaks, unencoded reflection in HTML responses,
 * evaluated templates, slow responses to delay payloads, injected response headers/cookies.
 */
public final class ApiSecurityAssertions {

    private ApiSecurityAssertions() {
    }

    public static List<String> analyse(Response response, String payload, long elapsedMs) {
        List<String> findings = new ArrayList<>();
        int status = response.getStatusCode();
        if (status >= 500) {
            findings.add("Server error HTTP " + status + " - input was not handled gracefully");
        }

        String contentType = response.getContentType() == null ? "" : response.getContentType().toLowerCase();
        boolean renderedByBrowser = contentType.contains("html") || contentType.contains("xml");
        findings.addAll(SecurityAssertions.analyseContent(response.asString(), payload, elapsedMs, renderedByBrowser));

        if (response.getHeaders().hasHeaderWithName("X-Injected")) {
            findings.add("Response header injection: X-Injected header present");
        }
        for (String cookie : response.getHeaders().getValues("Set-Cookie")) {
            if (cookie.contains("injected=1")) {
                findings.add("Response header injection: attacker-controlled Set-Cookie");
            }
        }
        return findings;
    }
}
