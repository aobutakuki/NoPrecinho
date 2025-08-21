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
import org.springframework.stereotype.Component;

@Component
public class WebScraper {


    //BASIC HTML
    public String tauste_connect(String region, String item) {
        try {
            // 1. Construct the full URL as a string
            String url = "https://tauste.com.br/" + region + "/" + item;
            System.out.println("[WebScraper] Attempting connection to: " + url);

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
            throw new RuntimeException("[WebScraper] Failed to connect to Tauste: " + e.getMessage(), e);
        }
    }




    //FOR JAVASCRIPT PAGES
    private  WebDriver driver;
    public WebScraper(){
        //setupBrowser();
    }

    public void setupBrowser() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1200");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
            this.driver = new ChromeDriver(options);
        }
    }
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
                System.out.println("[WebScraper] Screenshot saved to " + fileName);
            } catch (IOException ioException) {
                System.err.println("[WebScraper] Failed to save screenshot: " + ioException.getMessage());
            }
        }
    }
    public String carrefour_connect(String item) {
        setupBrowser();
            String url = "https://mercado.carrefour.com.br/" + item;

        System.out.println("[WebScraper] Attempting connection to: " + url);

            try{

                driver.get(url);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(7));

                try {
                    // Wait for the cookie button to be clickable and then click it.
                    // This XPath looks for a button that contains the text "Aceitar Cookies".
                    By cookieButtonSelector = By.xpath("//button[contains(text(), 'Aceitar Cookies')]");
                    wait.until(ExpectedConditions.elementToBeClickable(cookieButtonSelector)).click();
                    System.out.println("[WebScraper] Successfully clicked the cookie consent button.");
                } catch (TimeoutException e) {
                    // If the button doesn't appear, that's fine. We can continue.
                    System.out.println("[WebScraper] Cookie consent button not found or not needed.");
                }

                // 3. Once the element is present, get the final, fully-rendered page source.
                String pageSource = driver.getPageSource();



                return pageSource;


        } catch (Exception e) {
            //Gets Exception
                saveScreenshot("screenshot.png");
            closeConnection();
            throw new RuntimeException("[WebScraper] Failed to connect to Carrefour: " + e.getMessage(), e);
        }

    }

    public String shibata_connect(String shibata_item) {
        setupBrowser();
        try {
            // 1. Construct the full URL as a string
            String url = "https://www.loja.shibata.com.br/produto" +  "/" + shibata_item;
            System.out.println("[WebScraper] Attempting connection to: " + url);

            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(7));
            try {
                By cepInputSelector = By.cssSelector("input[placeholder='Digite seu CEP']");
                WebElement cepInput = wait.until(ExpectedConditions.visibilityOfElementLocated(cepInputSelector));

                System.out.println("[WebScraper] Location pop-up found. Entering CEP...");
                cepInput.sendKeys("12246130");

                By confirmButtonSelector = By.cssSelector("button.vip-button-raised");
                driver.findElement(confirmButtonSelector).click();
                System.out.println("[WebScraper] CEP submitted.");

                // **THE FIX**: Add a short, hard pause to allow the page to start its post-click loading.
                Thread.sleep(1000); // Wait for 1 second

            } catch (TimeoutException e) {
                System.out.println("[WebScraper] Location pop-up was not found, continuing...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
            }



            String pageSource = driver.getPageSource();



            return pageSource;

        } catch (Exception e) {
            // Jsoup throws an IOException on failure, which is cleaner to catch
            saveScreenshot("shibata_screenshot.png");
            closeConnection();
            throw new RuntimeException("[WebScraper] Failed to connect to Shibata: " + e.getMessage(), e);

        }
    }

    public String tausteScraper(String baseUrl){
        setupBrowser();

        try{
            System.out.println("[WebScraper / TausteSpider] Attempting connection to: " + baseUrl);

            driver.get(baseUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(7));

            try {
                By cepInputSelector = By.cssSelector("input[placeholder='Digite seu CEP']");
                WebElement cepInput = wait.until(ExpectedConditions.visibilityOfElementLocated(cepInputSelector));

                System.out.println("[WebScraper / TausteSpider] Location pop-up found. Entering CEP...");
                cepInput.sendKeys("12246130");

                By confirmButtonSelector = By.cssSelector("button.vip-button-raised");
            }catch(TimeoutException e){
                System.out.println("[WebScraper / TausteSpider] Location pop-up was not found, continuing...");
            }

            String pageSource = driver.getPageSource();
            System.out.println("[WebScraper / TausteSpiter] Source Code Found!");
            return pageSource;
        }catch (Exception e){
            System.out.printf("[WebScraper / TausteSpider] Failed to connect to Tauste: %s\n", e.getMessage());
        }
        closeConnection();
        return null;
    }
}
