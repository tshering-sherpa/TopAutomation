package com.topautomation.security.payloads;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/** Injection payload families. Each maps to a text file in src/main/resources/payloads/. */
public enum PayloadCategory {
    SQLI("sqli.txt"),
    NOSQLI("nosqli.txt"),
    XSS("xss.txt"),
    HTML_INJECTION("html-injection.txt"),
    COMMAND_INJECTION("command-injection.txt"),
    PATH_TRAVERSAL("path-traversal.txt"),
    LDAP("ldap.txt"),
    SSTI("ssti.txt"),
    XXE("xxe.txt"),
    CRLF("crlf.txt"),
    MALFORMED("malformed.txt"),
    /** Mobile-specific: deep links, intents, WebView bridges, local-storage (SQLite) probes, URL schemes. */
    MOBILE("mobile.txt");

    private final String fileName;

    PayloadCategory(String fileName) {
        this.fileName = fileName;
    }

    public String fileName() {
        return fileName;
    }

    /** Everything except the mobile-only family. */
    public static Set<PayloadCategory> webDefaults() {
        Set<PayloadCategory> set = EnumSet.allOf(PayloadCategory.class);
        set.remove(MOBILE);
        return set;
    }

    /** Parses "SQLI,XSS" style lists (case-insensitive). Blank returns the supplied defaults. */
    public static Set<PayloadCategory> parse(String csv, Set<PayloadCategory> defaults) {
        if (csv == null || csv.isBlank()) {
            return defaults;
        }
        Set<PayloadCategory> result = new LinkedHashSet<>();
        Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(s -> result.add(PayloadCategory.valueOf(s.toUpperCase())));
        return result;
    }
}
