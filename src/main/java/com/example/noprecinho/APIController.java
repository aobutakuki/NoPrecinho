package com.example.noprecinho;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class APIController {

    @Autowired
    private ItemsRepository itemsRepository;


    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private SupermarketRepository supermarketRepository;

    private HttpServletResponse response;

    @GetMapping("/items/add")
    public ResponseEntity<String> getItembyId(@RequestParam("id") Long newItemId,
                                              @CookieValue(name = "listOfItemsbyId", required = false) String encodedItemIds, // Read the encoded value
                                              HttpServletResponse response) {

        System.out.println("[APIController] Getting item by id: " + newItemId);
        ItemsDatabase item = itemsRepository.findById(newItemId)
                .orElseThrow(() -> new RuntimeException("[APIController] Entry not found with id: " + newItemId));
        System.out.println("[APIController] Item name: " + item.getItem_name());

        String decodedItemIds = "";
        // If the cookie exists, decode it first
        if (encodedItemIds != null) {
            decodedItemIds = URLDecoder.decode(encodedItemIds, StandardCharsets.UTF_8);
        }

        String updatedItemIds;
        // Now work with the decoded string
        if (!decodedItemIds.isEmpty()) {
            updatedItemIds = decodedItemIds + "," + newItemId.toString();
        } else {
            updatedItemIds = newItemId.toString();
        }


        // URL Encode the final string before setting the cookie
        String finalEncodedValue = URLEncoder.encode(updatedItemIds, StandardCharsets.UTF_8);

        Cookie listOfItemsbyIdCookie = new Cookie("listOfItemsbyId", finalEncodedValue);
        listOfItemsbyIdCookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
        listOfItemsbyIdCookie.setPath("/");

        response.addCookie(listOfItemsbyIdCookie);
        return ResponseEntity.ok("Item with ID " + newItemId + " added successfully.");
    }



}
