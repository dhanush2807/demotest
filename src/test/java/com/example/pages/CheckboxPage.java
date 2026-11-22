package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CheckboxPage extends BasePage {

    private static final By CHECKBOXES = By.cssSelector("#checkboxes input[type='checkbox']");

    public CheckboxPage(WebDriver driver) {
        super(driver);
    }

    public CheckboxPage open(String baseUrl) {
        driver.get(baseUrl + "/checkboxes");
        visible(CHECKBOXES);
        return this;
    }

    public CheckboxPage toggle(int index) {
        all().get(index).click();
        return this;
    }

    public boolean isChecked(int index) {
        return all().get(index).isSelected();
    }

    private List<WebElement> all() {
        return driver.findElements(CHECKBOXES);
    }
}
