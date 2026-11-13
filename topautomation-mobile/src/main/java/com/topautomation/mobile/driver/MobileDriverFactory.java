package com.topautomation.mobile.driver;

import com.topautomation.core.config.ConfigManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;

/** Creates AndroidDriver / IOSDriver sessions against the Appium server (setting: platform = android | ios). */
public final class MobileDriverFactory {

    private MobileDriverFactory() {
    }

    /** Creates an AndroidDriver or IOSDriver depending on the 'platform' setting. */
    public static WebDriver createMobileDriver() {
        String platform = ConfigManager.get("platform", "android").toLowerCase();
        URL server = toUrl(ConfigManager.get("appium.server.url", "http://127.0.0.1:4723"));
        boolean noReset = ConfigManager.getBoolean("mobile.no.reset", false);

        return switch (platform) {
            case "android" -> {
                UiAutomator2Options o = new UiAutomator2Options()
                        .setDeviceName(ConfigManager.get("android.device.name", "emulator-5554"))
                        .setNoReset(noReset)
                        .setAutoGrantPermissions(true)
                        .setNewCommandTimeout(Duration.ofSeconds(120));
                String version = ConfigManager.get("android.platform.version", "");
                if (!version.isBlank()) {
                    o.setPlatformVersion(version);
                }
                String app = ConfigManager.get("android.app", "");
                if (!app.isBlank()) {
                    o.setApp(resolveApp(app));
                }
                String pkg = ConfigManager.get("android.app.package", "");
                if (!pkg.isBlank()) {
                    o.setAppPackage(pkg);
                }
                String activity = ConfigManager.get("android.app.activity", "");
                if (!activity.isBlank()) {
                    o.setAppActivity(activity);
                }
                yield new AndroidDriver(server, o);
            }
            case "ios" -> {
                XCUITestOptions o = new XCUITestOptions()
                        .setDeviceName(ConfigManager.get("ios.device.name", "iPhone 15"))
                        .setNoReset(noReset)
                        .setNewCommandTimeout(Duration.ofSeconds(120));
                String version = ConfigManager.get("ios.platform.version", "");
                if (!version.isBlank()) {
                    o.setPlatformVersion(version);
                }
                String app = ConfigManager.get("ios.app", "");
                if (!app.isBlank()) {
                    o.setApp(resolveApp(app));
                }
                String bundle = ConfigManager.get("ios.bundle.id", "");
                if (!bundle.isBlank()) {
                    o.setBundleId(bundle);
                }
                yield new IOSDriver(server, o);
            }
            default -> throw new IllegalArgumentException("Unsupported platform '" + platform + "'. Use android | ios");
        };
    }

    /** Local paths become absolute; http(s) URLs (cloud storage) are passed through. */
    private static String resolveApp(String app) {
        if (app.startsWith("http://") || app.startsWith("https://") || app.startsWith("storage:")) {
            return app;
        }
        return Path.of(app).toAbsolutePath().toString();
    }

    private static URL toUrl(String url) {
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
}
