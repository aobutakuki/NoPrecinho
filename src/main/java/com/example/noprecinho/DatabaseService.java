package com.example.noprecinho;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseService.class);
    @Autowired
    private DatabaseRepository databaseRepository;
    @Autowired
    private ItemsRepository itemsRepository;

    public DatabaseRepository getDatabaseRepository() {
        return databaseRepository;
    }

    public String getURLbyId(Long listing_id){

        return databaseRepository.findById(listing_id).map(databaseInfo -> databaseInfo.getItem_url()).
                orElseThrow(() -> new RuntimeException("[DatabaseService] Entry not found with id: " + listing_id));

    }

    public Double getPricebyId(Long listing_id){
        return databaseRepository.findById(listing_id).map(databaseInfo -> databaseInfo.getItem_price()).
                orElseThrow(() -> new RuntimeException("[DatabaseService] Entry not found with id: " + listing_id));
    }

    public Integer getListingsCount(){
        return (int) databaseRepository.count();
    }



    public Boolean getAvailabilitybyId(Long listing_id){
        return databaseRepository.findById(listing_id).map(databaseInfo -> databaseInfo.getIs_availiable()).
                orElseThrow(() -> new RuntimeException("[DatabaseService] Entry not found with id: " + listing_id));
    }

    public Long getSupermarketId(Long listing_id){
        return databaseRepository.findById(listing_id).map(databaseInfo -> databaseInfo.getSupermarket().getSupermarket_id()).
                orElseThrow(() -> new RuntimeException("[DatabaseService] Entry not found with id: " + listing_id));
    }
    public String getItembyId(Long listing_id){
        return databaseRepository.findById(listing_id).map(databaseInfo -> databaseInfo.getItemsDatabase().getItem_name()).
                orElseThrow(() -> new RuntimeException("[DatabaseService] Entry not found with id: " + listing_id));
    }

    @Transactional
    public void updateItemPrice(Long listing_id, Double price){
        try{
            DatabaseInfo databaseInfo = databaseRepository.findById(listing_id).get();
            databaseInfo.setItem_price(price);
            databaseRepository.save(databaseInfo);
        }catch (Exception e){
            log.error("[DatabaseService] Error updating item price: " + e.getMessage());
            throw new RuntimeException("[DatabaseService] Error updating item price: " + e.getMessage());
        }finally {
            log.info("[DatabaseService] Item price updated successfully");
        }
    }

    @Transactional
    public void updateItemAvailability(Long listing_id, Boolean availability){
        try {
            DatabaseInfo databaseInfo = databaseRepository.findById(listing_id).get();
            databaseInfo.setItemAvailable(false);
            databaseRepository.save(databaseInfo);
        }catch (Exception e){
            log.error("[DatabaseService] Error updating item availability: " + e.getMessage());
            throw new RuntimeException("[DatabaseService] Error updating item availability: " + e.getMessage());
        }finally {
            log.info("[DatabaseService] Item availability updated successfully");
        }

    }

    public Boolean testConnection() {
        System.out.println("[DatabaseService] Testing connection to database...\n");
        try {
            // Simply try to execute a query. If it fails, an exception will be thrown.
            databaseRepository.count();
            log.info("[DatabaseService] Connection to database successful");
            return true;
        } catch (Exception e) {
            log.error("[DatabaseService] Connection to database failed: " + e.getMessage());
            return false;
        }
    }

    //Items
    public Integer getItemsCount(){return (int) itemsRepository.count();}

    public String getItembyIdItems(Long item_id){
        return itemsRepository.findById(item_id).map(itemsDatabase -> itemsDatabase.getItem_name()).
                orElseThrow(() -> new RuntimeException("[DatabaseService] Entry not found with id: " + item_id));
    }

    public String getBaseNamebyId(Long item_id){
        return itemsRepository.findById(item_id).map(itemsDatabase -> itemsDatabase.getItem_base_name())
                .orElseThrow(() -> new RuntimeException("[DatabaseService] Entry not found with id: " + item_id));

    }

    public String getItemImagebyId(Long item_id){
        return itemsRepository.findById(item_id).map(itemsDatabase -> itemsDatabase.getImage_link())
                .orElseThrow(() -> new RuntimeException("[DatabaseService] Entry not found with id: " + item_id));

    }

    public List<ItemsDatabase> getAllItems() {
        return itemsRepository.findAll();
    }

    public boolean itemExists(Long item_id){
        return itemsRepository.existsById(item_id);
    }

    public List<DatabaseInfo> getAllListings() {
        return databaseRepository.findAll();
    }
}
