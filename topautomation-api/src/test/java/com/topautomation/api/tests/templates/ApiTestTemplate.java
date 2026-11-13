package com.topautomation.api.tests.templates;

import com.topautomation.api.BookingFactory;
import com.topautomation.api.models.BookingResponse;
import com.topautomation.api.base.ApiBaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TEMPLATE - API TEST.   Copy this file, rename it, remove "enabled = false".
 *
 * 1. extends ApiBaseTest   -> gives you the API objects (bookingApi). No browser is started.
 * 2. groups = {...}        -> "api" (always) + any of "api-sanity", "api-functional", "api-regression", "api-security".
 * 3. Call the API object, keep the Response, then assert status + body.
 */
public class ApiTestTemplate extends ApiBaseTest {

    @Test(enabled = false, groups = {"api", "api-functional", "api-regression"}, description = "What this test proves")
    public void myFirstApiTest() {
        Response response = bookingApi.create(BookingFactory.random());          // 1. call the endpoint
        Assert.assertEquals(response.statusCode(), 200);                         // 2. check the status
        BookingResponse body = response.as(BookingResponse.class);               // 3. read the body as a Java object
        Assert.assertTrue(body.bookingid() > 0, "An id should be returned");
    }
}
