package com.topautomation.mobile.data;

import com.topautomation.core.data.ExcelData;
import org.testng.annotations.DataProvider;

/** Excel data providers for the MOBILE module. Data file: setting "mobile.testdata.file". */
public class MobileExcelDataProvider {

    private static final String FILE = "mobile.testdata.file";

    @DataProvider(name = "mobileLoginData")
    public static Object[][] mobileLoginData() {
        return ExcelData.rows(FILE, "MobileLogin");
    }
}
