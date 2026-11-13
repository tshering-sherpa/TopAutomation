package com.topautomation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

/** Page object for https://the-internet.herokuapp.com/dropdown - the worked example for "add a page + Excel sheet". */
public class DropdownPage extends BasePage {

    private final By dropdown = By.id("dropdown");

    public DropdownPage(WebDriver driver) {
        super(driver);
    }

    public DropdownPage open() {
        open("/dropdown");
        visible(dropdown);
        return this;
    }

    public DropdownPage choose(String visibleText) {
        new Select(visible(dropdown)).selectByVisibleText(visibleText);
        return this;
    }

    public String selectedOption() {
        return new Select(visible(dropdown)).getFirstSelectedOption().getText();
    }
}
