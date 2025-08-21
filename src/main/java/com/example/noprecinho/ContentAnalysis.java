package com.example.noprecinho;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ContentAnalysis {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private DatabaseRepository databaseRepository;
    @Autowired
    private ItemsRepository itemsRepository;


    public String tauste_price(String document, String item_base_name) {
        String price = null; //Set string to null to avoid null pointer exception

        Document doc = Jsoup.parse(document); //Parse document into Jsoup Document object
        System.out.println("document : " + document);

        boolean contains_item = containsItemName(document, item_base_name);
        System.out.println("[ContentAnalysis:] contains_item : " + contains_item);

        boolean contains_price = document.contains("price");
        System.out.println("[ContentAnalysis:] contains_price : " + contains_price);

        if(contains_price) {
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
        lowerCaseDoc = lowerCaseDoc.replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("-","").replaceAll("_","");

        lowerCaseItemName = lowerCaseItemName.replaceAll("[^a-zA-Z0-9\\s]", "");

        // 2. Now perform the check on the cleaned, lowercase strings.
        String[] searchWords = lowerCaseItemName.split("\\s+");
        Set<String> documentWordSet = Arrays.stream(lowerCaseDoc.split("\\s+"))
                .collect(Collectors.toSet());

        return Arrays.stream(searchWords).allMatch(documentWordSet::contains);

    }

    void tausteSpiderParse(Set<String> itemUrls) {

        for (String url : itemUrls) {
            try {
                // Be polite to the server, wait a bit between requests.
                Thread.sleep(500);

                System.out.println("[TausteSpiderParse] Processing URL: " + url);
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/58.0.3029.110 Safari/537.36")
                        .followRedirects(false)
                        .get();



                // --- 1. Scrape Data ---
                Element nameElement = doc.selectFirst("span[itemprop='name']");
                if (nameElement == null) {
                    System.out.println("--> Could not find item name. Skipping URL: " + url);
                    continue; // Skip to the next URL in the loop
                }
                String base_name = nameElement.text();
                String item_name = getItemName(base_name);

                Element image = doc.select("img.fotorama__img").first();
                String image_link = (image != null) ? image.attr("abs:src") : "default-image.jpg";

                // --- 2. Create Item (if it doesn't exist) ---
                if (!itemsRepository.existsByBase_name(base_name)) {
                    databaseService.createItem(item_name, base_name, image_link);
                    System.out.println("--> CREATED new item: " + base_name);
                }

                // --- 3. Create Listing (if it doesn't exist) ---
                Optional<ItemsDatabase> itemOptional = itemsRepository.findByBase_name(base_name);

                // Use .isPresent() to check the Optional correctly
                if (itemOptional.isPresent()) {
                    ItemsDatabase item = itemOptional.get();
                    Long tausteSupermarketId = 1L; // Assuming Tauste is ID 1 in your DB


                        String baseUrl = getUrlSlug(url);
                        databaseService.createListing(baseUrl, null, true, tausteSupermarketId, item.getItem_id());
                        System.out.println("--> CREATED new listing for item: " + item_name);

                        System.out.println("--> Listing already exists for item: " + item_name);

                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                System.err.println("[TausteSpiderParse] Thread was interrupted.");
                break; // Exit the loop
            } catch (Exception e) {
                System.err.println("[TausteSpiderParse] Failed to process URL " + url + ": " + e.getMessage());
            }
        }
        System.out.println("[TausteSpiderParse] Finished processing all URLs.");
    }

    String getItemName(String itemName) {

        if (itemName == null || itemName.isBlank()) {
            return "";
        }
// Split the string by spaces, take the first 3 words, and join them back.
        return Arrays.stream(itemName.split("\\s+"))
                .limit(3)
                .collect(Collectors.joining(" "));
    }


    public String getUrlSlug(String fullUrl) {
        // Return an empty string if the URL is null or empty
        if (fullUrl == null || fullUrl.isEmpty()) {
            return "";
        }
        // Find the index of the last '/' character
        int lastSlashIndex = fullUrl.lastIndexOf('/');
        // If a slash is found, return the part of the string after it
        if (lastSlashIndex != -1) {
            return fullUrl.substring(lastSlashIndex + 1);
        } else {
            // If no slash is found, return the original string
            return fullUrl;
        }
    }


}

