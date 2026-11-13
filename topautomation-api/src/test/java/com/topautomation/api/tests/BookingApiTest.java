package com.topautomation.api.tests;

import com.topautomation.api.BookingFactory;
import com.topautomation.api.models.Booking;
import com.topautomation.api.models.BookingDates;
import com.topautomation.api.models.BookingResponse;
import com.topautomation.api.base.ApiBaseTest;
import com.topautomation.api.data.ApiExcelDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * API test TEMPLATE against the public restful-booker demo API. Copy the pattern:
 * call the API object -> assert status, body fields, schema, headers, timing.
 */
public class BookingApiTest extends ApiBaseTest {

    @Test(groups = {"api", "api-sanity"}, description = "Health check endpoint is up")
    public void pingReturnsCreated() {
        Response response = bookingApi.ping();
        Assert.assertEquals(response.statusCode(), 201);
    }

    @Test(groups = {"api", "api-sanity", "api-functional", "api-regression"},
            description = "Create a booking and read it back")
    public void createBookingReturnsIdAndEchoesData() {
        Booking request = BookingFactory.random();

        Response created = bookingApi.create(request);
        Assert.assertEquals(created.statusCode(), 200);
        BookingResponse body = created.as(BookingResponse.class);
        Assert.assertTrue(body.bookingid() > 0, "bookingid should be positive");
        Assert.assertEquals(body.booking(), request);

        Response fetched = bookingApi.get(body.bookingid());
        Assert.assertEquals(fetched.statusCode(), 200);
        Assert.assertEquals(fetched.as(Booking.class), request);
    }

    @Test(groups = {"api", "api-functional", "api-regression"}, description = "Response matches the JSON schema")
    public void createBookingResponseMatchesSchema() {
        bookingApi.create(BookingFactory.valid())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-response.json"))
                .body("booking.firstname", equalTo("Jane"))
                .body("bookingid", notNullValue());
    }

    @Test(groups = {"api", "api-functional", "api-regression"}, description = "Full CRUD lifecycle with auth token")
    public void bookingCrudLifecycle() {
        int id = bookingApi.create(BookingFactory.random()).as(BookingResponse.class).bookingid();

        Booking updated = new Booking("Updated", "Name", 999, false,
                new BookingDates("2027-01-10", "2027-01-12"), "None");
        Response put = bookingApi.update(id, updated);
        Assert.assertEquals(put.statusCode(), 200);
        Assert.assertEquals(put.as(Booking.class), updated);

        Assert.assertEquals(bookingApi.delete(id).statusCode(), 201);
        Assert.assertEquals(bookingApi.get(id).statusCode(), 404, "deleted booking must be gone");
    }

    @Test(groups = {"api", "api-functional", "api-regression"}, description = "Search by first name finds the booking")
    public void searchByFirstName() {
        Booking booking = BookingFactory.random();
        int id = bookingApi.create(booking).as(BookingResponse.class).bookingid();

        List<Integer> ids = bookingApi.search(Map.of("firstname", booking.firstname()))
                .jsonPath().getList("bookingid", Integer.class);
        Assert.assertTrue(ids.contains(id), "search results " + ids + " should contain " + id);
    }

    @Test(groups = {"api", "api-functional", "api-regression"}, description = "Unknown booking returns 404")
    public void unknownBookingReturns404() {
        Assert.assertEquals(bookingApi.get(999_999_999).statusCode(), 404);
    }

    @Test(groups = {"api", "api-functional", "api-regression"}, description = "Update without a token is forbidden")
    public void updateWithoutTokenIsForbidden() {
        Response response = bookingApi.updateWithoutAuth(1, BookingFactory.valid());
        Assert.assertEquals(response.statusCode(), 403);
    }

    @Test(groups = {"api", "api-functional", "api-regression"}, description = "Bad credentials never return a token")
    public void badCredentialsReturnNoToken() {
        Response response = bookingApi.authenticate("admin", "definitely-wrong");
        response.then().body(containsString("Bad credentials"));
        Assert.assertNull(response.jsonPath().getString("token"), "no token may be issued");
    }

    @Test(groups = {"api", "api-functional", "api-regression"}, description = "Response time stays reasonable")
    public void responseTimeIsAcceptable() {
        Response response = bookingApi.ping();
        Assert.assertTrue(response.time() < 3000, "ping took " + response.time() + " ms");
    }

    @Test(groups = {"api", "api-functional", "api-regression"},
            dataProvider = "apiBookingData", dataProviderClass = ApiExcelDataProvider.class,
            description = "Create-booking scenarios driven by the ApiBooking sheet")
    public void createBookingDrivenByExcel(Map<String, String> data) {
        Booking request = new Booking(
                data.get("Firstname"), data.get("Lastname"), Integer.parseInt(data.get("TotalPrice")),
                Boolean.parseBoolean(data.get("DepositPaid")),
                new BookingDates(data.get("CheckIn"), data.get("CheckOut")), data.get("Needs"));

        Response response = bookingApi.create(request);
        Assert.assertEquals(response.statusCode(), Integer.parseInt(data.get("ExpectedStatus")),
                data.get("TestCaseId") + ": unexpected status");
        if (response.statusCode() == 200) {
            Assert.assertEquals(response.as(BookingResponse.class).booking(), request);
        }
    }
}
