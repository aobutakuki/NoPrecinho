package com.example.noprecinho;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ContentAnalysis {
    public String tauste_price(String document, String item_base_name) {
        String price = null; //Set string to null to avoid null pointer exception

        Document doc = Jsoup.parse(document); //Parse document into Jsoup Document object
        System.out.println("document : " + document);

        boolean contains_item = document.contains(item_base_name);
        System.out.println("contains_item : " + contains_item);

        boolean contains_price = document.contains("price");
        System.out.println("contains_price : " + contains_price);

        if(contains_item && contains_price) {
            Element item_price;
            item_price = doc.select("meta[itemprop=price]").first(); //Select the first price meta tag

            price = item_price.attr("content");
            System.out.println("price : " + price);
        }
        return price;
    }

    public String carrefour_price(String document, String item_base_name) {
        String price = null; //Set string to null to avoid null pointer exception

        Document doc = Jsoup.parse(document); //Parse document into Jsoup Document object
        System.out.println("document : " + document);

        boolean contains_item = document.contains(item_base_name);
        System.out.println("contains_item : " + contains_item);

        boolean contains_price = document.contains("price");
        System.out.println("contains_price : " + contains_price);

        if(contains_item && contains_price) {
            Element item_price;
            item_price = doc.select("span.text-pdp-price").first(); //Select the first price span tag

            price = item_price.text();
            System.out.println("price : " + price);
        }
        return price;
    }

    public String shibata_price(String document,String item_base_name) {
        String price = null; //Set string to null to avoid null pointer exception

        Document doc = Jsoup.parse(document); //Parse document into Jsoup Document object
        System.out.println("document : " + document);

        boolean contains_item = document.contains(item_base_name);
        System.out.println("contains_item : " + contains_item);

        boolean contains_price = document.contains("preco");
        System.out.println("contains_price : " + contains_price);

        if(contains_item && contains_price) {

            Element priceSpan = doc.select("span[data-cy=preco]").first();

            String rawText = priceSpan.text();
            System.out.println("Raw text found: " + rawText);

            // 3. Clean the text to get only the number
            // Replaces "R$ " and the non-breaking space (&nbsp;) with an empty string
            price = rawText.replace("R$", "").replace("\u00a0", "").trim();
            System.out.println("Cleaned price: " + price);
        }
        return price;
    }
}
