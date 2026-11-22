package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class DropdownPage extends BasePage {

    private static final By DROPDOWN = By.id("dropdown");

    public DropdownPage(WebDriver driver) {
        super(driver);
    }

    public DropdownPage open(String baseUrl) {
        driver.get(baseUrl + "/dropdown");
        return this;
    }

    public DropdownPage select(String visibleText) {
        new Select(visible(DROPDOWN)).selectByVisibleText(visibleText);
        return this;
    }

    public String selected() {
        return new Select(visible(DROPDOWN)).getFirstSelectedOption().getText();
    }
}
