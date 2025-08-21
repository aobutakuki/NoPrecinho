package com.example.noprecinho;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class HTMLController {

    @Autowired // Required to create an actual DB object instead of a java object
    private DatabaseService databaseService;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private SupermarketRepository supermarketRepository;



    @GetMapping("/")
    public String index(Model model, @CookieValue(name = "listOfItemsbyId", required = false) String encodedItemIds) {

        //Check if the cookie exists
        Set<Long> addedItemIds = Collections.emptySet(); // Default to an empty set
        if (encodedItemIds != null) {
            String decodedItemIds = URLDecoder.decode(encodedItemIds, StandardCharsets.UTF_8);
            addedItemIds = Arrays.stream(decodedItemIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        }

        // Add the set of IDs to the model
        model.addAttribute("addedItemIds", addedItemIds);


        //Item list
        List<ItemsDatabase> allItems = databaseService.getAllItems();

        model.addAttribute("items", allItems);
        model.addAttribute("itemsCount", databaseService.getItemsCount());
        model.addAttribute("listingsCount", databaseService.getListingsCount());



        return "index";
    }



    @GetMapping("/list")
    public String showShoppingList(@CookieValue(name = "listOfItemsbyId", required = false) String encodedItemIds, Model model) {
        List<ItemsDatabase> allItems = new ArrayList<>();

        if (encodedItemIds != null) {
            String decodedItemIds = URLDecoder.decode(encodedItemIds, StandardCharsets.UTF_8);

            List<Long> itemIds = Arrays.stream(decodedItemIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            allItems = itemsRepository.findAllById(itemIds);

            // 1. Calculate prices
            Map<Long, Double> supermarketPrices = databaseService.findSupermarketPrices(itemIds);

            // 2. Find the cheapest supermarket
            Optional<Map.Entry<Long, Double>> cheapest = supermarketPrices.entrySet()
                    .stream()
                    .min(Map.Entry.comparingByValue());

            // 3. Add EVERYTHING to the model
            model.addAttribute("supermarketPrices", supermarketPrices);
            cheapest.ifPresent(entry ->
                    model.addAttribute("cheapestSupermarketId", entry.getKey()));
        }

        //Get list of all supermarkets
        List<SupermarketInfo> allSupermarkets = databaseService.getAllSupermarkets();

        //Map it to id
        Map<Long, SupermarketInfo> supermarketMap = allSupermarkets.stream()
                .collect(Collectors.toMap(SupermarketInfo::getSupermarket_id, Function.identity()));


        model.addAttribute("allItems", allItems);
        model.addAttribute("supermarketMap", supermarketMap);

        //Check if the cookie exists - IMPLEMENT FUNCTION NEXT
        Set<Long> addedItemIds = Collections.emptySet(); // Default to an empty set
        if (encodedItemIds != null) {
            String decodedItemIds = URLDecoder.decode(encodedItemIds, StandardCharsets.UTF_8);
            addedItemIds = Arrays.stream(decodedItemIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        }

        // Add the set of IDs to the model
        model.addAttribute("addedItemIds", addedItemIds);
        return "shopping-list"; // 4. Return the name of your new HTML template
    }
}
