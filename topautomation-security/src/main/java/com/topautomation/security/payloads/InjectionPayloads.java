package com.topautomation.security.payloads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central access point for the injection payload library.
 *
 * <pre>
 *   List&lt;String&gt; sql = InjectionPayloads.get(PayloadCategory.SQLI);
 *   List&lt;String&gt; all = InjectionPayloads.getAll(PayloadCategory.XSS, PayloadCategory.SQLI);
 *   String huge = InjectionPayloads.longString(10_000);
 * </pre>
 *
 * Payloads live in plain text files (src/main/resources/payloads/*.txt): one per line,
 * blank lines and lines starting with "##" are ignored. Add your own lines any time.
 *
 * ONLY use against applications you own or are explicitly authorised to test.
 * The built-in payloads are detection probes (non-destructive); keep it that way when adding your own.
 */
public final class InjectionPayloads {

    private static final Map<PayloadCategory, List<String>> CACHE = new ConcurrentHashMap<>();

    private InjectionPayloads() {
    }

    public static List<String> get(PayloadCategory category) {
        return CACHE.computeIfAbsent(category, InjectionPayloads::load);
    }

    public static List<String> getAll(PayloadCategory... categories) {
        return getAll(List.of(categories));
    }

    public static List<String> getAll(Collection<PayloadCategory> categories) {
        List<String> all = new ArrayList<>();
        categories.forEach(c -> all.addAll(get(c)));
        return all;
    }

    /** A string of exactly {@code length} characters, for field-length / overflow tests. */
    public static String longString(int length) {
        return "A".repeat(Math.max(0, length));
    }

    private static List<String> load(PayloadCategory category) {
        String path = "payloads/" + category.fileName();
        try (InputStream in = InjectionPayloads.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Payload file not found on classpath: " + path);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            List<String> payloads = new ArrayList<>(reader.lines()
                    .filter(line -> !line.isBlank() && !line.startsWith("##"))
                    .toList());
            if (category == PayloadCategory.MALFORMED) {
                payloads.add(longString(256));
                payloads.add(longString(1_024));
                payloads.add(longString(10_000));
            }
            return List.copyOf(payloads);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read payload file " + path, e);
        }
    }
}
