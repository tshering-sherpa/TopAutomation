package com.topautomation.api;

import com.topautomation.core.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/** One place that defines how every API request is built (base URL, JSON, timeouts, logging, auth). */
public final class ApiConfig {

    private ApiConfig() {
    }

    /** Unauthenticated request spec. Build a fresh one per request. */
    public static RequestSpecification baseSpec() {
        int timeout = ConfigManager.getInt("api.timeout.ms", 15000);
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("api.base.url", "https://restful-booker.herokuapp.com"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new ApiLogFilter())
                .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeout)
                        .setParam("http.socket.timeout", timeout)));
        String basePath = ConfigManager.get("api.base.path", "");
        if (!basePath.isBlank()) {
            builder.setBasePath(basePath);
        }
        if (ConfigManager.getBoolean("api.relaxed.https", false)) {
            builder.setRelaxedHTTPSValidation();
        }
        return builder.build();
    }

    /** Base spec plus the auth header produced by {@link AuthManager}. */
    public static RequestSpecification authSpec() {
        return io.restassured.RestAssured.given().spec(baseSpec())
                .header(ConfigManager.get("api.auth.header", "Authorization"), authHeaderValue());
    }

    /**
     * Header value = prefix + token. A space is added after a word prefix such as "Bearer" automatically;
     * prefixes ending in "=" (e.g. "token=") or already ending in a space are used as written.
     */
    static String authHeaderValue() {
        String prefix = ConfigManager.get("api.auth.prefix", "Bearer");
        if (!prefix.endsWith("=") && !prefix.endsWith(" ")) {
            prefix = prefix + " ";
        }
        return prefix + AuthManager.token();
    }
}
