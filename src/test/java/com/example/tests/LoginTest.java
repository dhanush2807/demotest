package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.LoginPage;
import com.example.utils.Config;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "Valid user can log in")
    public void validLogin() {
        LoginPage page = new LoginPage(driver())
                .open(BASE_URL)
                .loginAs(Config.get("username"), Config.get("password"));

        assertTrue(page.flashMessage().contains("You logged into a secure area!"));
        assertTrue(page.isLogoutVisible(), "Logout link should be visible after login");
    }

    @DataProvider(name = "badLogins", parallel = true)
    public Object[][] badLogins() {
        return new Object[][]{
                {"wronguser", "SuperSecretPassword!", "Your username is invalid!"},
                {"tomsmith", "wrongpass", "Your password is invalid!"},
        };
    }

    @Test(dataProvider = "badLogins", groups = "regression", description = "Invalid logins show an error")
    public void invalidLogin(String user, String pass, String expectedError) {
        LoginPage page = new LoginPage(driver()).open(BASE_URL).loginAs(user, pass);
        assertTrue(page.flashMessage().contains(expectedError),
                "Expected error '" + expectedError + "' but got: " + page.flashMessage());
    }
}
