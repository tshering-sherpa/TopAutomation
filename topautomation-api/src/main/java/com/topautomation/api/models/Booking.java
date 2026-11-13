package com.topautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/** Request/response body of the sample API (restful-booker). Replace with your own API's models. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Booking(String firstname, String lastname, int totalprice, boolean depositpaid,
                      BookingDates bookingdates, String additionalneeds) {
}
