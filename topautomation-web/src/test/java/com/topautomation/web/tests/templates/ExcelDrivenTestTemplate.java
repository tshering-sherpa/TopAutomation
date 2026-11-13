package com.topautomation.web.tests.templates;

import com.topautomation.web.base.BaseTest;
import com.topautomation.web.data.WebExcelDataProvider;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * TEMPLATE - EXCEL-DRIVEN TEST.  Each row of the Excel sheet becomes one run of the test method.
 *
 *   dataProvider      = name given to the @DataProvider in WebExcelDataProvider.java
 *   dataProviderClass = WebExcelDataProvider.class
 *   the parameter     = Map<String,String>: key = column header, value = cell text
 *
 * See README.TXT section "EXCEL DATA PROVIDERS" for how to add a new sheet + provider.
 */
public class ExcelDrivenTestTemplate extends BaseTest {

    @Test(enabled = false, groups = {"web", "web-functional"},
            dataProvider = "dropdownData", dataProviderClass = WebExcelDataProvider.class)
    public void myExcelDrivenTest(Map<String, String> data) {
        String testCaseId = data.get("TestCaseId");          // column header "TestCaseId"
        String option = data.get("OptionToSelect");          // column header "OptionToSelect"
        log.info("Running {} with option {}", testCaseId, option);
        // ... use page objects and assertions here
    }
}
