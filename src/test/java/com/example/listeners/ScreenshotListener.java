package com.example.listeners;

import com.example.base.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Saves a PNG to target/screenshots/ whenever a test fails. CI uploads this folder. */
public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver();
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            String name = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName()
                    + "_" + System.currentTimeMillis() + ".png";
            Files.write(dir.resolve(name), png);
            System.out.println("Screenshot saved: " + dir.resolve(name));
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not take screenshot: " + e.getMessage());
        }
    }
}
