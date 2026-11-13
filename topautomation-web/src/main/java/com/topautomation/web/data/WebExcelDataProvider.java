package com.topautomation.web.data;

import com.topautomation.core.data.ExcelData;
import org.testng.annotations.DataProvider;

/**
 * Excel data providers for the WEB module. Data file: setting "web.testdata.file"
 * (default src/test/resources/testdata/web-testdata.xlsx). Each Excel row = one test run, passed to the
 * test as Map&lt;String,String&gt; (column header -> cell text). Rows with Run = N are skipped.
 *
 * To add a sheet: add a tab in web-testdata.xlsx and a one-line provider below.
 */
public class WebExcelDataProvider {

    private static final String FILE = "web.testdata.file";

    @DataProvider(name = "loginData", parallel = true)
    public static Object[][] loginData() {
        return ExcelData.rows(FILE, "Login");
    }

    @DataProvider(name = "registrationData", parallel = true)
    public static Object[][] registrationData() {
        return ExcelData.rows(FILE, "Registration");
    }

    @DataProvider(name = "dropdownData", parallel = true)
    public static Object[][] dropdownData() {
        return ExcelData.rows(FILE, "Dropdown");
    }
}
