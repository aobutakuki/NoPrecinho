package com.example.noprecinho;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

import java.util.List;

@Controller
public class HTMLController {

    @Autowired // Required to create an actual DB object instead of a java object
    private DatabaseService databaseService;



    @GetMapping("/")
    public String index(Model model) {

        //Item list
        List<ItemsDatabase> allItems = databaseService.getAllItems();

        model.addAttribute("items", allItems);
        model.addAttribute("itemsCount", databaseService.getItemsCount());
        model.addAttribute("listingsCount", databaseService.getListingsCount());

        return "index";
    }
}
