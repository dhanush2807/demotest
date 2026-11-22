package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.CheckboxPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CheckboxTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "Checkboxes can be toggled")
    public void toggleCheckboxes() {
        CheckboxPage page = new CheckboxPage(driver()).open(BASE_URL);

        assertFalse(page.isChecked(0), "Checkbox 1 starts unchecked");
        page.toggle(0);
        assertTrue(page.isChecked(0), "Checkbox 1 should be checked after click");
    }
}
