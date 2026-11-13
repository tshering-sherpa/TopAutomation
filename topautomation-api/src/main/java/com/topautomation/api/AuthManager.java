package com.topautomation.api;

import com.topautomation.core.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

/** Logs in once per run (thread-safe) and caches the token. Fully driven by api.auth.* settings. */
public final class AuthManager {

    private static final Object LOCK = new Object();
    private static volatile String token;

    private AuthManager() {
    }

    public static String token() {
        if (token == null) {
            synchronized (LOCK) {
                if (token == null) {
                    token = fetchToken();
                }
            }
        }
        return token;
    }

    /** Forget the cached token (e.g. after testing logout / expiry). */
    public static void reset() {
        synchronized (LOCK) {
            token = null;
        }
    }

    private static String fetchToken() {
        String userField = ConfigManager.get("api.auth.username.field", "username");
        String passField = ConfigManager.get("api.auth.password.field", "password");
        Response response = RestAssured.given().spec(ApiConfig.baseSpec())
                .body(Map.of(
                        userField, ConfigManager.get("api.auth.username", ""),
                        passField, ConfigManager.get("api.auth.password", "")))
                .post(ConfigManager.get("api.auth.login.path", "/auth"));

        String value = response.jsonPath().getString(ConfigManager.get("api.auth.token.jsonpath", "token"));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Could not obtain an auth token. HTTP " + response.statusCode()
                    + " - check api.auth.* settings.");
        }
        return value;
    }
}
