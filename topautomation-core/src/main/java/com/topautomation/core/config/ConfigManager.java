package com.topautomation.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Layered configuration. Settings are looked up in this order (first hit wins):
 * <ol>
 *   <li>-Dkey=value system properties</li>
 *   <li>environment variables (api.auth.password -> API_AUTH_PASSWORD) - for secrets / CI</li>
 *   <li>config files, later files overriding earlier ones:
 *       core.properties, core-{env}.properties, then for each module present on the classpath
 *       (web, mobile, api, security): {module}.properties and {module}-{env}.properties</li>
 * </ol>
 * Every module ships its OWN config files (names are unique per module, so they never collide).
 */
public final class ConfigManager {

    private static final List<String> MODULES = List.of("web", "mobile", "api", "security");
    private static final Properties PROPS = new Properties();

    static {
        load("config/core.properties", true);
        String env = System.getProperty("env");
        if (env == null || env.isBlank()) {
            env = PROPS.getProperty("env", "qa");
        }
        PROPS.setProperty("env", env);
        load("config/core-" + env + ".properties", false);
        for (String module : MODULES) {
            load("config/" + module + ".properties", false);
            load("config/" + module + "-" + env + ".properties", false);
        }
    }

    private ConfigManager() {
    }

    private static void load(String path, boolean required) {
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                if (required) {
                    throw new IllegalStateException("Missing config file on classpath: " + path);
                }
                return;
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config file " + path, e);
        }
    }

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys;
        }
        String env = System.getenv(key.toUpperCase().replace('.', '_').replace('-', '_'));
        if (env != null && !env.isBlank()) {
            return env;
        }
        return PROPS.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String v = get(key);
        return (v == null || v.isBlank()) ? defaultValue : v.trim();
    }

    public static int getInt(String key, int defaultValue) {
        String v = get(key);
        if (v == null || v.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Config '" + key + "' must be an integer but was: " + v, e);
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String v = get(key);
        return (v == null || v.isBlank()) ? defaultValue : Boolean.parseBoolean(v.trim());
    }
}
