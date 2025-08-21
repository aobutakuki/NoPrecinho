package com.example.noprecinho;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class NoPrecinhoApplication implements CommandLineRunner {

    @Autowired // Required to create an actual DB object instead of a java object
    private DatabaseService databaseService;

    @Autowired // <-- ADD THIS to inject your ContentAnalysis bean
    private ContentAnalysis contentAnalysis;

    @Autowired
    private TausteSpider tausteSpider;

    @Autowired
    private ItemsRepository itemsRepository;

    public static void main(String[] args) {
        SpringApplication.run(NoPrecinhoApplication.class, args);
        //FIX ERROR TO CHECK FOR TAUSTE (TEMP FIXED)
    }

    //Logic here
    @Override
    public void run(String... args) throws Exception {

        if(databaseService.testConnection()){
            //update_db(databaseService);
           // Set<String> urlsToParse = new HashSet<>();
            //urlsToParse = tausteSpider.crawl();
            //contentAnalysis.tausteSpiderParse(urlsToParse);
            //updateAllItemImages();
        }
    }



     private static void update_db(DatabaseService databaseService){

         WebScraper webScraper = new WebScraper();
         ContentAnalysis contentAnalysis = new ContentAnalysis();
         String document;
         //Fetch the list of ALL actual listings from the database first.
         List<DatabaseInfo> allListings = databaseService.getAllListings();

         long totalStartTime = System.nanoTime(); //Total time
            int count = 0;
         //Loop through the collection of real listing objects.
         for (DatabaseInfo listing : allListings) {

             long startTime = System.nanoTime(); // Resets time to measure each loop

             System.out.println("---------------------\n[Main_DB_Update]");
             System.out.println("Processing listing ID: @@@[ " + listing.getListing_id() + " ] @@@\n");

             System.out.println("[COUNT] Item: " + (++count) + " | " + allListings.size() + "\n");

             // 3. Use the 'listing' object directly instead of looking up by 'i'.
             if (listing.getIs_availiable()) {
                 System.out.println("Item at listing ID " + listing.getListing_id() + " is available");
                 String item_url = listing.getItem_url();
                 String item_name = listing.getItem().getItem_name(); // Get data from the related item

                 System.out.println("Item URL: " + item_url);
                 System.out.println("Item name: " + item_name);

                 String price = null;
                 if (listing.getSupermarket().getSupermarket_id() == 1) {
                     document = webScraper.tauste_connect("saojosedoscampos", item_url);
                     price = contentAnalysis.tauste_price(document, item_name);
                 }
                 if (listing.getSupermarket().getSupermarket_id() == 2) {
                     document = webScraper.carrefour_connect(item_url);
                     price = contentAnalysis.carrefour_price(document, item_name);
                 }
                 if (listing.getSupermarket().getSupermarket_id() == 3) {
                     document = webScraper.shibata_connect(item_url);
                     price = contentAnalysis.shibata_price(document, item_name);
                 }

                 System.out.println("\n@@ Item price: " + price +
                         "| Supermarket ID: " + listing.getSupermarket().getSupermarket_id() + " @\n");

                 System.out.println("Updating item price...\n");

                 if (price != null) {
                     Double priceDouble = contentAnalysis.StringToDouble(price);

                     if (priceDouble != null) {
                         databaseService.updateItemPrice(listing.getListing_id(), priceDouble);
                     }
                 } else {
                     System.out.println("--> Price not found for item: " + item_name + ". Skipping update.");
                     databaseService.updateItemAvailability(listing.getListing_id(), false);
                 }
             } else {
                 System.out.println("Item at listing ID " + listing.getListing_id() + " is not available");
             }

             long endTime = System.nanoTime();
             long durationMs = (endTime - startTime) / 1_000_000; //Convert from nano to actual time


             System.out.println("[CLOCK] Time Elapased: " + durationMs + " ms\n");


             System.out.println("---------------------\n");
         }

         System.out.println(">>>>> Database updated ! >>>>>>>");

         long totalEndTime = System.nanoTime();
         long totalDurationSec = (totalEndTime - totalStartTime) / 1_000_000_000;

         System.out.println("[CLOCK] Total Time (Seconds): " + totalDurationSec + " s\n");
         System.out.println("[CLOCK] Total Time (Minutes): " + (totalDurationSec / 60 ) + " min\n");

         webScraper.closeConnection();
     }


    public void updateAllItemImages() {
        System.out.println("[UpdateService] Starting image update process...");
        List<ItemsDatabase> allItems = itemsRepository.findAll();
        int updatedCount = 0;

        for (ItemsDatabase item : allItems) {
            try {
                // Find the Tauste-specific listing for this item (assuming Tauste ID is 1L)
                Optional<DatabaseInfo> tausteListingOpt = item.getListings().stream()
                        .filter(listing -> listing.getSupermarket().getSupermarket_id() == 1L)
                        .findFirst();

                if (tausteListingOpt.isPresent()) {
                    DatabaseInfo tausteListing = tausteListingOpt.get();
                    // Construct the full URL to scrape
                    String productUrl = "https://tauste.com.br/saojosedoscampos/" + tausteListing.getItem_url();

                    System.out.println("Checking item: " + item.getItem_name());

                    // Use Jsoup to fetch and parse the product page
                    Document doc = Jsoup.connect(productUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/58.0.3029.110 Safari/537.36")
                            .followRedirects(false)
                            .get();

                    // Find the image URL from the <meta> tag
                    Element imageMetaTag = doc.selectFirst("meta[property=og:image]");

                    if (imageMetaTag != null) {
                        String newImageLink = imageMetaTag.attr("content");
                        String oldImageLink = item.getImage_link();

                        // Check if the link is new and not empty
                        if (newImageLink != null && !newImageLink.isBlank() && !newImageLink.equals(oldImageLink)) {
                            item.setImage_link(newImageLink);
                            itemsRepository.save(item);
                            System.out.println("  -> UPDATED image for: " + item.getItem_name());
                            updatedCount++;
                        }
                    }
                }

                // Be polite to the server and wait before the next request
                Thread.sleep(50);

            } catch (Exception e) {
                System.err.println("  -> FAILED to update image for '" + item.getItem_name() + "': " + e.getMessage());
            }
        }
        System.out.println("[UpdateService] Image update process finished. " + updatedCount + " of " + allItems.size() + " items updated.");
    }


}



