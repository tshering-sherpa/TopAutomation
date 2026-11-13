package com.topautomation.api.base;

import com.topautomation.api.endpoints.BookingApi;
import com.topautomation.core.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;

/** Base class for API tests: no browser, just API objects. Add one field per API resource you test. */
public abstract class ApiBaseTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final BookingApi bookingApi = new BookingApi();

    @BeforeClass(alwaysRun = true)
    public void logTarget() {
        log.info("API under test: {}", ConfigManager.get("api.base.url"));
    }
}
