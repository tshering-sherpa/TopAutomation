package com.topautomation.core.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.topautomation.core.config.ConfigManager;
import com.topautomation.core.utils.HtmlUtils;
import com.topautomation.core.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/** Turns TestNG events into the Extent HTML report (with failure screenshots). Registered in every suite XML. */
public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.get();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        ExtentTest test = ExtentManager.get().createTest(
                HtmlUtils.escape(displayName(result)),
                HtmlUtils.escape(description == null ? "" : description));
        String[] groups = result.getMethod().getGroups();
        if (groups.length > 0) {
            test.assignCategory(groups);
        }
        ExtentTestManager.set(test);
        LOG.info("START {}", displayName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ExtentTestManager.get();
        if (test != null) {
            test.log(Status.PASS, "Test passed");
        }
        LOG.info("PASS  {}", displayName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentTestManager.get();
        LOG.error("FAIL  {} - {}", displayName(result), result.getThrowable() == null ? "" : result.getThrowable().getMessage());
        if (test == null) {
            return;
        }
        test.log(Status.FAIL, "<pre>" + HtmlUtils.escape(stackTrace(result.getThrowable())) + "</pre>");
        if (ConfigManager.getBoolean("screenshot.on.failure", true)) {
            String base64 = ScreenshotUtils.base64();
            if (base64 != null) {
                test.fail("Screenshot at failure",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64, "Failure").build());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentTestManager.get();
        if (test == null) {
            return;
        }
        String reason = result.getThrowable() == null
                ? "Skipped"
                : "Skipped (or failed and queued for retry): " + result.getThrowable().getMessage();
        test.log(Status.SKIP, HtmlUtils.escape(reason));
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    /** Test name shown in the report; includes data-driven parameters (Excel TestCaseId or payload). */
    static String displayName(ITestResult result) {
        String name = result.getMethod().getMethodName();
        Object[] params = result.getParameters();
        if (params == null || params.length == 0) {
            return name;
        }
        if (params[0] instanceof Map<?, ?> row) {
            Object id = row.get("TestCaseId");
            return id == null ? name : name + " [" + id + "]";
        }
        StringBuilder sb = new StringBuilder(name).append(" [");
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(HtmlUtils.truncate(String.valueOf(params[i]), 60));
        }
        return sb.append("]").toString();
    }

    private static String stackTrace(Throwable t) {
        if (t == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
