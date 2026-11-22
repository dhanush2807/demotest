package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.DropdownPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DropdownTest extends BaseTest {

    @Test(groups = "regression", description = "User can choose a dropdown option")
    public void selectOption() {
        DropdownPage page = new DropdownPage(driver()).open(BASE_URL).select("Option 2");
        assertEquals(page.selected(), "Option 2");
    }
}
