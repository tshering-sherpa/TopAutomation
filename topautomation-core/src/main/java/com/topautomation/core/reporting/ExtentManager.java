package com.topautomation.core.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.topautomation.core.config.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Creates the single ExtentReports instance for the run of ONE module:
 * &lt;module&gt;/target/reports/TopAutomation-&lt;module&gt;_&lt;timestamp&gt;.html
 */
public final class ExtentManager {

    private static ExtentReports extent;

    private ExtentManager() {
    }

    public static synchronized ExtentReports get() {
        if (extent == null) {
            String dir = ConfigManager.get("report.dir", "target/reports");
            try {
                Files.createDirectories(Path.of(dir));
            } catch (IOException e) {
                throw new IllegalStateException("Cannot create report directory " + dir, e);
            }
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String module = ConfigManager.get("module.name", "core");
            String title = "TopAutomation - " + module.substring(0, 1).toUpperCase() + module.substring(1) + " Report";
            ExtentSparkReporter spark = new ExtentSparkReporter(dir + "/TopAutomation-" + module + "_" + stamp + ".html");
            spark.config().setDocumentTitle(title);
            spark.config().setReportName(title);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setEncoding("utf-8");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Module", ConfigManager.get("module.name", "core"));
            extent.setSystemInfo("Environment", ConfigManager.get("env", "qa"));
            extent.setSystemInfo("Base URL", ConfigManager.get("base.url", ""));
            extent.setSystemInfo("Browser", ConfigManager.get("browser", "chrome"));
            extent.setSystemInfo("Headless", String.valueOf(ConfigManager.getBoolean("headless", false)));
            extent.setSystemInfo("Mobile platform", ConfigManager.get("platform", "android"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
        }
        return extent;
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
