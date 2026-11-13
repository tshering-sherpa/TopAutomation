package com.topautomation.api.endpoints;

import com.topautomation.api.ApiConfig;
import com.topautomation.api.models.AuthRequest;
import com.topautomation.api.models.Booking;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

/**
 * "Page Object" for the API: one method per endpoint, each returning the raw Response so tests decide what to assert.
 * Create one class per API resource (UserApi, OrderApi...) following this pattern.
 */
public class BookingApi {

    public Response ping() {
        return RestAssured.given().spec(ApiConfig.baseSpec()).get("/ping");
    }

    /** Ping that also sends one custom header (header-injection tests). */
    public Response pingWithHeader(String name, String value) {
        return RestAssured.given().spec(ApiConfig.baseSpec()).header(name, value).get("/ping");
    }

    public Response authenticate(String username, String password) {
        return RestAssured.given().spec(ApiConfig.baseSpec()).body(new AuthRequest(username, password)).post("/auth");
    }

    public Response create(Booking booking) {
        return RestAssured.given().spec(ApiConfig.baseSpec()).body(booking).post("/booking");
    }

    public Response get(int id) {
        return RestAssured.given().spec(ApiConfig.baseSpec()).get("/booking/{id}", id);
    }

    /** Path segment as an arbitrary string (URL-encoded by REST Assured) - used for path-parameter injection. */
    public Response getByRawId(String rawId) {
        return RestAssured.given().spec(ApiConfig.baseSpec()).pathParam("id", rawId).get("/booking/{id}");
    }

    public Response search(Map<String, ?> queryParams) {
        return RestAssured.given().spec(ApiConfig.baseSpec()).queryParams(queryParams).get("/booking");
    }

    public Response update(int id, Booking booking) {
        return RestAssured.given().spec(ApiConfig.authSpec()).body(booking).put("/booking/{id}", id);
    }

    public Response updateWithoutAuth(int id, Booking booking) {
        return RestAssured.given().spec(ApiConfig.baseSpec()).body(booking).put("/booking/{id}", id);
    }

    public Response delete(int id) {
        return RestAssured.given().spec(ApiConfig.authSpec()).delete("/booking/{id}", id);
    }
}
