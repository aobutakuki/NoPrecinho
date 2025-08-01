package com.example.noprecinho;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.time.Duration;
import java.util.Map;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Connection;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;


public class WebScraper {

    public String tauste_connect(String region, String item) {
        try {
            // 1. Construct the full URL as a string
            String url = "https://tauste.com.br/" + region + "/" + item;
            System.out.println("Attempting connection to: " + url);

            // 2. Use Jsoup to connect, set the User-Agent, and get the document
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/58.0.3029.110 Safari/537.36")
                    .followRedirects(false)
                    .get();

            // 3. Return the entire HTML content as a String
            return doc.outerHtml();

        } catch (IOException e) {
            // Jsoup throws an IOException on failure, which is cleaner to catch
            throw new RuntimeException("Failed to connect to Tauste: " + e.getMessage(), e);
        }
    }
    private  WebDriver driver;

    public void closeConnection() {
        if(driver != null) {
            driver.quit();
        }
    }

    private void saveScreenshot(String fileName) {
        if (driver instanceof TakesScreenshot) {
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(screenshotFile, new File(fileName));
                System.out.println("Screenshot saved to " + fileName);
            } catch (IOException ioException) {
                System.err.println("Failed to save screenshot: " + ioException.getMessage());
            }
        }
    }
    public String carrefour_connect(String item) {
            String url = "https://mercado.carrefour.com.br/" + item + "/p";

            //Setup Selenium driver
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1200");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

            this.driver = new ChromeDriver(options);

            try{

                driver.get(url);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

                try {
                    // Wait for the cookie button to be clickable and then click it.
                    // This XPath looks for a button that contains the text "Aceitar Cookies".
                    By cookieButtonSelector = By.xpath("//button[contains(text(), 'Aceitar Cookies')]");
                    wait.until(ExpectedConditions.elementToBeClickable(cookieButtonSelector)).click();
                    System.out.println("Successfully clicked the cookie consent button.");
                } catch (TimeoutException e) {
                    // If the button doesn't appear, that's fine. We can continue.
                    System.out.println("Cookie consent button not found or not needed.");
                }

                // 3. Once the element is present, get the final, fully-rendered page source.
                String pageSource = driver.getPageSource();


                closeConnection();
                return pageSource;


        } catch (Exception e) {
            //Gets Exception
                saveScreenshot("screenshot.png");
            closeConnection();
            throw new RuntimeException("Failed to connect to Carrefour: " + e.getMessage(), e);
        }

    }

    public String shibata_connect(String shibata_item) {
        try {
            // 1. Construct the full URL as a string
            String url = "https://www.loja.shibata.com.br/produto" +  "/" + shibata_item;
            System.out.println("Attempting connection to: " + url);

            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1200");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

            this.driver = new ChromeDriver(options);

            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            String pageSource = driver.getPageSource();
            closeConnection();
            return pageSource;

        } catch (Exception e) {
            // Jsoup throws an IOException on failure, which is cleaner to catch
            closeConnection();
            throw new RuntimeException("Failed to connect to Shibata: " + e.getMessage(), e);

        }
    }
}
