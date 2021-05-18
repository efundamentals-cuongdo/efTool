package com.efundamental;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Sleeper;

import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

/**
 * Hello world!
 *
 */
public class App {
    public static int getScrollHeight(JavascriptExecutor js) {
        return ((Long) js.executeScript("return document.body.scrollTop;")).intValue();
    }

    public static int getClientHeight(JavascriptExecutor js) {
        return ((Long) js.executeScript("return document.body.scrollHeight;")).intValue();
    }

    public static void scroll(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // int start = 0;
        // int end = ((Long) js.executeScript("return
        // document.body.scrollHeight;")).intValue();
        // int scrollHeight = speed;
        Integer current = getScrollHeight(js);
        while (current <= getClientHeight(js)) {
            js.executeScript("window.scrollBy(0,300);");
            current += 300;
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // while (scrollHeight < end) {
        // js.executeScript("window.scrollTo(arguments[0], arguments[1])", start,
        // scrollHeight);
        // start = scrollHeight;
        // scrollHeight += speed;
        // }

    }

    public static void scroll(WebDriver driver, int speed) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int start = 0;
        int end = ((Long) js.executeScript("return document.body.scrollHeight;")).intValue();
        int scrollHeight = speed;

        while (scrollHeight < end) {
            js.executeScript("window.scrollTo(arguments[0], arguments[1])", start, scrollHeight);
            start = scrollHeight;
            scrollHeight += speed;
        }

    }

    public static void crawl(String url, String cssLink, String cssStrong, String preLink) {
        // System.setProperty("webdriver.gecko.driver", "C:\\geckodriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--window-size=1920,1200");
        
        List<Word> lstWords = new ArrayList<Word>();
        for (int i = 0; i < 32; i++) {
            String baseUrl = url + "&page=" + (i+1);
            System.out.println("Open:" + baseUrl);
            WebDriver driver = new ChromeDriver(options);
            driver.get(baseUrl);
            scroll(driver);
            Document doc = Jsoup.parse(driver.getPageSource());
            driver.close();
            driver.quit();
            Elements links = doc.select(cssLink);// ".v4-global-product-item a"); // a with href

            for (Element link : links) {
                String linkHref = link.attr("href");

                linkHref = preLink + linkHref;
                System.out.println("Open:" + linkHref);
                driver = new ChromeDriver(options);
                driver.get(linkHref);
                scroll(driver, 500);
                Document doc2 = Jsoup.parse(driver.getPageSource());
                Elements strongs = doc2.select(cssStrong); // ".prod-content-box strong" a with href
                for (Element strong : strongs) {
                    Word word = new Word();
                    if (!word.checkExist(strong.text(), lstWords)) {
                        word.setUrl(linkHref);
                        word.setWord(strong.text());
                        lstWords.add(word);
                    }
                }

                driver.close();
                driver.quit();
            }
        }

        String fileContents = "";
        for (Word word : lstWords) {
            fileContents += word.getWord() + "," + word.getUrl() + "\r\n";
        }

        try {
            FileWriter myWriter = new FileWriter("words.csv");
            myWriter.write(fileContents);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // comment the above 2 lines and uncomment below 2 lines to use Chrome
        // System.setProperty("webdriver.chrome.driver","G:\\chromedriver.exe");
        // WebDriver driver = new ChromeDriver();

        String baseUrl = "https://www.istegelsin.com/kategori/icecek_L/gazli-icecek_L0101";

        String preLink = "https://www.istegelsin.com";
        // driver.manage().window().maximize();

        // WebElement elementOption = driver
        // .findElement(By.cssSelector(".filter-item:first-child .b-contain
        // [type=checkbox][checked] +div"));
        // elementOption.click();

        // WebElement element = driver.findElement(By.cssSelector(".more-item"));
        // element.click();

        // List<WebElement> elements =
        // driver.findElements(By.cssSelector(".filter-item:first-child .b-contain
        // [type=checkbox]:not([checked]) +div"));
        // for (WebElement webElement : elements) {
        // webElement.click();
        // }

        // String linkHref = links.first().attr("href");
        // linkHref = "https://www.istegelsin.com" + linkHref;
        // WebDriver driverDetail = new ChromeDriver(options);
        // driverDetail.get(linkHref);
        // scroll(driverDetail, 50);
        // driverDetail.close();

        String link = ".product-listing-item a";
        preLink = "https://www.carrefoursa.com/";
        String cssStrong = ".productInfo__Classifications strong";

        crawl("https://www.carrefoursa.com/icecekler/c/1409", link, cssStrong, preLink);
    }
}
