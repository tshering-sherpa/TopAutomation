package com.topautomation.core.reporting;

import com.aventstack.extentreports.ExtentTest;

/** One ExtentTest per thread so parallel tests log to the right report entry. */
public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    private ExtentTestManager() {
    }

    public static ExtentTest get() {
        return TEST.get();
    }

    public static void set(ExtentTest test) {
        TEST.set(test);
    }

    public static void remove() {
        TEST.remove();
    }

    /** Convenience for adding a step to the current test's report entry: ExtentTestManager.info("..."). */
    public static void info(String message) {
        ExtentTest t = TEST.get();
        if (t != null) {
            t.info(com.topautomation.core.utils.HtmlUtils.escape(message));
        }
    }
}
