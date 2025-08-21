package com.example.noprecinho;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class TausteSpider {

    //Set instead of List to avoid duplicates
    private Set<String> ItemUrls = new HashSet<>();


    private final WebScraper webScraper;

    @Autowired
    public TausteSpider(WebScraper webScraper) {
        this.webScraper = webScraper;
    }


    public Set<String> crawl(){
        String nexturl = "https://tauste.com.br/saojosedoscampos/padaria.html";

        try{
            while(nexturl != null && !nexturl.isEmpty()){
                System.out.println("[TAUSTE CRAWLER] Crawling page: " + nexturl);

                String pageSource = webScraper.tausteScraper(nexturl);
                Document doc = Jsoup.parse(pageSource);

                // 1. Select all the main list item containers
                Elements productItems = doc.select("li.product.item.product-item");

                System.out.println("Found " + productItems.size() + " product cards on the page.");

                // 2. Loop through each product card
                for (Element item : productItems) {

                    // 3. Find the specific link INSIDE this card
                    Element linkElement = item.selectFirst(".product-item-name a.product-item-link");

                    // 4. Check if the link was found before trying to use it
                    if (linkElement != null) {
                        String productUrl = linkElement.attr("abs:href");
                        ItemUrls.add(productUrl);
                        System.out.println("  -> Found product URL: " + productUrl);
                    }
                }

                Element nextPage = doc.select("a.action.next").first();
                if(nextPage != null){
                    nexturl = nextPage.attr("abs:href");
                }else{
                    nexturl = null;
                }

            }

        } catch (Exception e) {
            System.err.println("[TAUSTE CRAWLER] Failed to crawl page: " + nexturl);
            e.printStackTrace();
        }
        System.out.println("[TAUSTE CRAWLER] Found " + ItemUrls.size() + " items");


        webScraper.closeConnection();
        return ItemUrls;
    }
}
