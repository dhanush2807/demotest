package com.example.base;

import com.example.utils.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;

/**
 * Creates one WebDriver per thread so tests can run in parallel safely.
 * Selenium Manager (built into Selenium 4.6+) downloads the right driver automatically.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void initDriver() {
        String browser = Config.get("browser").toLowerCase();
        boolean headless = Config.getBoolean("headless");
        AbstractDriverOptions<?> options = buildOptions(browser, headless);

        String gridUrl = Config.get("gridUrl");
        WebDriver driver;
        if (!gridUrl.isEmpty()) {
            try {
                driver = new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid gridUrl: " + gridUrl, e);
            }
        } else {
            driver = switch (browser) {
                case "firefox" -> new FirefoxDriver((FirefoxOptions) options);
                case "edge" -> new EdgeDriver((EdgeOptions) options);
                default -> new ChromeDriver((ChromeOptions) options);
            };
        }
        DRIVER.set(driver);
    }

    private static AbstractDriverOptions<?> buildOptions(String browser, boolean headless) {
        switch (browser) {
            case "firefox": {
                FirefoxOptions o = new FirefoxOptions();
                if (headless) {
                    o.addArguments("-headless");
                }
                o.addArguments("--width=1920", "--height=1080");
                return o;
            }
            case "edge": {
                EdgeOptions o = new EdgeOptions();
                if (headless) {
                    o.addArguments("--headless=new");
                }
                o.addArguments("--window-size=1920,1080");
                return o;
            }
            default: {
                ChromeOptions o = new ChromeOptions();
                if (headless) {
                    o.addArguments("--headless=new");
                }
                // --no-sandbox and --disable-dev-shm-usage stop Chrome crashing on Linux CI / Docker
                o.addArguments("--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
                return o;
            }
        }
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
