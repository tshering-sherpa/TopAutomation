package com.topautomation.api;

import com.topautomation.api.models.Booking;
import com.topautomation.api.models.BookingDates;

import java.util.UUID;

/** Test-data builders so tests never hand-write request bodies. */
public final class BookingFactory {

    private BookingFactory() {
    }

    public static Booking valid() {
        return new Booking("Jane", "Doe", 150, true, new BookingDates("2026-12-01", "2026-12-05"), "Breakfast");
    }

    public static Booking random() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        return new Booking("First" + id, "Last" + id, 100 + (int) (Math.random() * 900), false,
                new BookingDates("2026-11-01", "2026-11-03"), "Late checkout");
    }

    /** A valid booking whose every free-text field carries the given value (used for injection tests). */
    public static Booking withTextEverywhere(String text) {
        return new Booking(text, text, 100, true, new BookingDates("2026-12-01", "2026-12-05"), text);
    }
}
