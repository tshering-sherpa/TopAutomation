package com.topautomation.api.data;

import com.topautomation.core.data.ExcelData;
import org.testng.annotations.DataProvider;

/** Excel data providers for the API module. Data file: setting "api.testdata.file". */
public class ApiExcelDataProvider {

    private static final String FILE = "api.testdata.file";

    @DataProvider(name = "apiBookingData", parallel = true)
    public static Object[][] apiBookingData() {
        return ExcelData.rows(FILE, "ApiBooking");
    }
}
