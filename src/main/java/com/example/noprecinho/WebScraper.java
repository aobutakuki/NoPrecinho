package com.example.noprecinho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
}
