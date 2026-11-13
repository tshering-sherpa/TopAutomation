package com.topautomation.core.reporting;

import com.topautomation.core.config.ConfigManager;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Re-runs a failed test up to retry.count times. Security tests are never retried (a finding is a finding). */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Map<String, AtomicInteger> ATTEMPTS = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        int max = ConfigManager.getInt("retry.count", 1);
        if (max <= 0 || Arrays.stream(result.getMethod().getGroups()).anyMatch(g -> g.startsWith("security"))) {
            return false;
        }
        String key = result.getMethod().getQualifiedName() + Arrays.deepToString(result.getParameters());
        return ATTEMPTS.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet() <= max;
    }
}
