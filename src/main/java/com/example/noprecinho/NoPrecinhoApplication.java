package com.example.noprecinho;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

        for(int i = 1; i <= databaseService.getListingsCount(); i++){
            System.out.println("---------------------\n[Main_DB_Update]");
            System.out.println("Entry number: @@@[ " + i + " ]@@@\n");
            if(databaseService.getAvailabilitybyId(Long.valueOf(i))){


                System.out.println("Item at " + i + " is available");
                String item_url = databaseService.getURLbyId(Long.valueOf(i));


                System.out.println("Item URL: " + item_url);
                System.out.println("Item name: " + databaseService.getItembyId(Long.valueOf(i)));


                String price = null;
               if(databaseService.getSupermarketId(Long.valueOf(i)) == 1){

                   document = webScraper.tauste_connect("saojosedoscampos",item_url);
                   price = contentAnalysis.tauste_price(document, databaseService.getItembyId(Long.valueOf(i)));

               }
               if(databaseService.getSupermarketId(Long.valueOf(i)) == 2){

                   document = webScraper.carrefour_connect(item_url);
                   price = contentAnalysis.carrefour_price(document, databaseService.getItembyId(Long.valueOf(i)));

               }
               if(databaseService.getSupermarketId(Long.valueOf(i)) == 3){

                   document = webScraper.shibata_connect(item_url);
                   price = contentAnalysis.shibata_price(document, databaseService.getItembyId(Long.valueOf(i)));

               }

                System.out.println("\n@@ Item price: " + price +
                        "| Supermarket ID: " + databaseService.getSupermarketId(Long.valueOf(i)) + " @\n");

                System.out.println("Updating item price...\n");
                if (price != null && !price.isBlank()) {
                    //Add this for parsable format
                    String parsablePrice = price.replace("R$", "")  // 1. Remove the currency symbol
                            .replace(",", ".")   // 2. Replace the comma with a period
                            .trim();              // 3. Remove any leading/trailing spaces

                    //Parse Price to Double
                    Double priceDouble = contentAnalysis.StringToDouble(parsablePrice);



                    //safely use priceDouble to update database
                    databaseService.updateItemPrice(Long.valueOf(i), priceDouble);
                } else {
                    // Log an error or a warning and continue to the next item
                    System.out.println("--> Price not found for item: " + databaseService.getItembyId(Long.valueOf(i)) + ". Skipping update.");
                    databaseService.updateItemAvailability(Long.valueOf(i), false);
                }
            }
            else{
                System.out.println("Item at " + i + " is not available");
            }

            System.out.println("---------------------\n");
        }

        System.out.println(">>>>> Database updated ! >>>>>>>");
        webScraper.closeConnection();

    }


}
