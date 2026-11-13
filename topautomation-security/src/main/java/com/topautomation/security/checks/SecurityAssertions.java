package com.topautomation.security.checks;

import com.topautomation.core.config.ConfigManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Heuristic checks that run after a payload was submitted. They return a list of findings;
 * an EMPTY list means "nothing suspicious observed". These are signals, not proof - always
 * triage a finding manually before raising a defect, and use a real DAST scanner for depth.
 */
public final class SecurityAssertions {

    private static final Map<String, Pattern> LEAK_SIGNATURES = new LinkedHashMap<>();
    private static final Pattern TIME_BASED_PAYLOAD =
            Pattern.compile("(?i)(sleep\\s*\\(|sleep\\s+\\d|waitfor\\s+delay|pg_sleep|benchmark\\s*\\(|ping\\s+-)");
    private static final Pattern INPUT_TAGS = Pattern.compile("(?is)<input\\b[^>]*>|<textarea\\b.*?</textarea>");

    static {
        LEAK_SIGNATURES.put("SQL error disclosed",
                Pattern.compile("(?i)(sql syntax|sqlstate|unclosed quotation mark|ora-\\d{5}|pg::syntaxerror|sqlite3?::|odbc driver|syntax error at or near|unterminated quoted string)"));
        LEAK_SIGNATURES.put("Stack trace / debug error disclosed",
                Pattern.compile("(?i)(traceback \\(most recent call last\\)|exception in thread|at [\\w.$]+\\([\\w]+\\.java:\\d+\\)|nullpointerexception|stack trace:|fatal error:.*on line \\d+)"));
        LEAK_SIGNATURES.put("NoSQL error disclosed",
                Pattern.compile("(?i)(mongoerror|bsonerror|cast to objectid failed)"));
        LEAK_SIGNATURES.put("OS command output (id) in response",
                Pattern.compile("uid=\\d+\\([\\w-]+\\) gid=\\d+"));
        LEAK_SIGNATURES.put("File content disclosed (/etc/passwd)",
                Pattern.compile("root:[x*]:0:0:"));
        LEAK_SIGNATURES.put("File content disclosed (win.ini)",
                Pattern.compile("(?i)\\[(fonts|extensions|mci extensions)\\]"));
        LEAK_SIGNATURES.put("LDAP error disclosed",
                Pattern.compile("(?i)(ldap_search|invalid dn syntax|javax\\.naming)"));
    }

    private SecurityAssertions() {
    }

    /** Web: also detects a JavaScript dialog opened by the payload (strong XSS signal). */
    public static List<String> analyse(WebDriver driver, String payload, long elapsedMs) {
        List<String> findings = new ArrayList<>();
        try {
            Alert alert = driver.switchTo().alert();
            findings.add("JavaScript dialog executed after payload (XSS): \"" + alert.getText() + "\"");
            alert.dismiss();
        } catch (NoAlertPresentException ignored) {
            // expected - no dialog
        }
        String source;
        try {
            source = driver.getPageSource();
        } catch (WebDriverException e) {
            source = "";
        }
        findings.addAll(analyseContent(source, payload, elapsedMs));
        return findings;
    }

    /** Platform-neutral analysis of any response text (HTML, mobile page-source XML, alert text...). */
    public static List<String> analyseContent(String content, String payload, long elapsedMs) {
        return analyseContent(content, payload, elapsedMs, true);
    }

    /**
     * @param checkReflection false for JSON/API responses, where echoing input back is normal and not
     *                        exploitable unless the response is served as HTML.
     */
    public static List<String> analyseContent(String content, String payload, long elapsedMs, boolean checkReflection) {
        List<String> findings = new ArrayList<>();
        String all = content == null ? "" : content;
        // Re-populated form fields legitimately contain the submitted text, so ignore them.
        String body = INPUT_TAGS.matcher(all).replaceAll("");

        LEAK_SIGNATURES.forEach((name, pattern) -> {
            if (pattern.matcher(all).find()) {
                findings.add(name);
            }
        });
        if (checkReflection && payload.contains("<") && body.contains(payload)) {
            findings.add("Payload reflected unencoded in response (possible XSS / HTML injection)");
        }
        if (payload.contains("1337*1337") && body.contains("1787569")) {
            findings.add("Template expression was evaluated server-side (possible SSTI)");
        }
        int threshold = ConfigManager.getInt("security.time.threshold.ms", 4500);
        if (TIME_BASED_PAYLOAD.matcher(payload).find() && elapsedMs >= threshold) {
            findings.add("Response took " + elapsedMs + " ms after a delay payload (possible time-based injection)");
        }
        return findings;
    }
}
