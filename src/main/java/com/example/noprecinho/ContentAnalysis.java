package com.example.noprecinho;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ContentAnalysis {
    public String tauste_price(String document, String item_base_name) {
        String price = null;
        Document doc = Jsoup.parse(document);
        System.out.println("document : " + document);
        boolean contains_item = document.contains(item_base_name);
        System.out.println("contains_item : " + contains_item);

        boolean contains_price = document.contains("price");
        System.out.println("contains_price : " + contains_price);

        if(contains_item && contains_price) {
            Element item_price;
            item_price = doc.select("meta[itemprop=price]").first();

            price = item_price.attr("content");
            System.out.println("price : " + price);
        }
        return price;
    }
}
