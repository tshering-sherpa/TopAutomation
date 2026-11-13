package com.topautomation.mobile.driver;

import com.topautomation.core.config.ConfigManager;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Optionally starts a LOCAL Appium server before mobile tests (appium.server.autostart=true).
 * Requires Appium to be installed (npm i -g appium) and the drivers installed. If a server already
 * answers on the configured port it is reused and left running. Stopped automatically when the run ends.
 */
public final class AppiumServerManager {

    private static final Logger LOG = LogManager.getLogger(AppiumServerManager.class);
    private static AppiumDriverLocalService service;

    private AppiumServerManager() {
    }

    public static synchronized void startIfEnabled() {
        if (!ConfigManager.getBoolean("appium.server.autostart", false) || service != null) {
            return;
        }
        URI uri = URI.create(ConfigManager.get("appium.server.url", "http://127.0.0.1:4723"));
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? 4723 : uri.getPort();

        if (!"127.0.0.1".equals(host) && !"localhost".equalsIgnoreCase(host)) {
            LOG.info("appium.server.url is remote ({}); not starting a local server.", host);
            return;
        }
        if (isListening(host, port)) {
            LOG.info("Appium already running on {}:{}; reusing it.", host, port);
            return;
        }
        try {
            Files.createDirectories(Path.of("target", "logs"));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create log directory", e);
        }
        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress(host)
                .usingPort(port)
                .withLogFile(new File("target/logs/appium.log"));
        service = AppiumDriverLocalService.buildService(builder);
        service.start();
        LOG.info("Started Appium server at {}", service.getUrl());
        Runtime.getRuntime().addShutdownHook(new Thread(AppiumServerManager::stop));
    }

    public static synchronized void stop() {
        if (service != null && service.isRunning()) {
            service.stop();
        }
        service = null;
    }

    private static boolean isListening(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 500);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
