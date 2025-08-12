package com.example.noprecinho;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class NoPrecinhoApplication implements CommandLineRunner {

    @Autowired // Required to create an actual DB object instead of a java object
    private DatabaseService databaseService;


    public static void main(String[] args) {
        SpringApplication.run(NoPrecinhoApplication.class, args);
        //FIX ERROR TO CHECK FOR NULL ENTRIES IN DB, IF THERE IS NO ENTRY AT NUMBER X in THE LOOP THE PROGRAM WILL CRASH
    }

    //Logic here
    @Override
    public void run(String... args) throws Exception {

        if(databaseService.testConnection()){
            //update_db(databaseService);

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
                 if (price != null && !price.isBlank()) {
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



}



