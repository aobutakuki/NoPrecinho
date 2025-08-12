package com.example.noprecinho;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ContentAnalysis {
    public String tauste_price(String document, String item_base_name) {
        String price = null; //Set string to null to avoid null pointer exception

        Document doc = Jsoup.parse(document); //Parse document into Jsoup Document object
        //System.out.println("document : " + document);

        boolean contains_item = containsItemName(document, item_base_name);
        System.out.println("[ContentAnalysis:] contains_item : " + contains_item);

        boolean contains_price = document.contains("price");
        System.out.println("[ContentAnalysis:] contains_price : " + contains_price);

        if(contains_item && contains_price) {
            Element item_price;
            item_price = doc.select("meta[itemprop=price]").first(); //Select the first price meta tag

            price = item_price.attr("content");
            System.out.println("[ContentAnalysis:] price : " + price);
        }
        return price;
    }

    public String carrefour_price(String document, String item_base_name) {
        String price = null; //Set string to null to avoid null pointer exception

        Document doc = Jsoup.parse(document); //Parse document into Jsoup Document object
        //System.out.println("document : " + document);

        boolean contains_item = containsItemName(document, item_base_name);
        System.out.println("[ContentAnalysis:] contains_item : " + contains_item);

        boolean contains_price = document.contains("price");
        System.out.println("[ContentAnalysis:] contains_price : " + contains_price);

        if(contains_item && contains_price) {
            Element item_price;
            item_price = doc.select("span.text-pdp-price").first(); //Select the first price span tag

            if(item_price != null) {
                price = item_price.text();
                System.out.println("[ContentAnalysis:] price : " + price);
            }
            else {
                System.out.println("[ContentAnalysis:] price is null == item not available");
            }
        }
        return price;
    }

    public String shibata_price(String document,String item_base_name) {
        String price = null; //Set string to null to avoid null pointer exception

        Document doc = Jsoup.parse(document); //Parse document into Jsoup Document object
        //System.out.println("document : " + document);

        boolean contains_item = containsItemName(document, item_base_name);
        System.out.println("[ContentAnalysis:] contains_item : " + contains_item);

        boolean contains_price = document.contains("preco");
        System.out.println("[ContentAnalysis:] contains_price : " + contains_price);

        if(contains_item && contains_price) {

            Element priceSpan = doc.select("span[data-cy=preco]").first();

            String rawText = priceSpan.text();
            System.out.println("[ContentAnalysis:] Raw text found: " + rawText);

            // 3. Clean the text to get only the number
            // Replaces "R$ " and the non-breaking space (&nbsp;) with an empty string
            price = rawText.replace("R$", "").replace("\u00a0", "").trim();
            System.out.println("[ContentAnalysis:] Cleaned price: " + price);
        }
        return price;
    }

    public Double StringToDouble(String price) {
        // Return null immediately if the input is bad to prevent errors
        if (price == null || price.isBlank()) {
            return null;
        }

        // Clean the string of ALL non-numeric characters before parsing
        String cleanedPrice = price
                .replace("R$", "")  // 1. Remove the currency symbol
                .replace(",", ".")   // 2. Replace the comma with a period
                .trim();              // 3. Remove any leading/trailing spaces

        try {
            // Now, parse the fully cleaned string
            return Double.parseDouble(cleanedPrice);
        } catch (NumberFormatException e) {
            System.err.println("Could not parse cleaned price: '" + cleanedPrice + "'");
            return null; // Return null if the cleaned string is still not a valid number
        }
    }

    public Boolean containsItemName(String document, String item_name){
        if(document.contains(item_name)){
            return true;
        }

        String lowerCaseDoc = document.toLowerCase();
        String lowerCaseItemName = item_name.toLowerCase();

        // Optional but recommended: Remove punctuation for a cleaner match.
        // This regex leaves only letters, numbers, and spaces.
        lowerCaseDoc = lowerCaseDoc.replaceAll("[^a-zA-Z0-9\\s]", "");
        lowerCaseItemName = lowerCaseItemName.replaceAll("[^a-zA-Z0-9\\s]", "");

        // 2. Now perform the check on the cleaned, lowercase strings.
        String[] searchWords = lowerCaseItemName.split("\\s+");
        Set<String> documentWordSet = Arrays.stream(lowerCaseDoc.split("\\s+"))
                .collect(Collectors.toSet());

        return Arrays.stream(searchWords).allMatch(documentWordSet::contains);

    }
}
