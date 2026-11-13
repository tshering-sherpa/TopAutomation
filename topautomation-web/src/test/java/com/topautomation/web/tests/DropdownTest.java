package com.topautomation.web.tests;

import com.topautomation.web.base.BaseTest;
import com.topautomation.web.data.WebExcelDataProvider;
import com.topautomation.web.pages.DropdownPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/** Worked example of an Excel-driven test that uses the "Dropdown" sheet in web-testdata.xlsx. */
public class DropdownTest extends BaseTest {

    @Test(groups = {"web", "web-functional", "web-regression"},
            dataProvider = "dropdownData", dataProviderClass = WebExcelDataProvider.class,
            description = "Each option in the Dropdown sheet can be selected")
    public void selectOptionFromExcel(Map<String, String> data) {
        DropdownPage page = new DropdownPage(driver()).open().choose(data.get("OptionToSelect"));
        Assert.assertEquals(page.selectedOption(), data.get("ExpectedSelected"),
                data.get("TestCaseId") + ": wrong option selected");
    }
}
