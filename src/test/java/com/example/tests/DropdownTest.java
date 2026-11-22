package com.example.tests;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import com.example.base.BaseTest;
import com.example.pages.DropdownPage;

public class DropdownTest extends BaseTest {

    @Test(groups = "regression", description = "User can choose a dropdown option")
    public void selectption() {
        DropdownPage page = new DropdownPage(driver()).open(BASE_URL).select("Option 2");
        assertEquals(page.selected(), "Option 2");
    }
}
