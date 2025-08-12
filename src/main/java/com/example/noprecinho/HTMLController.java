package com.example.noprecinho;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

@Controller
public class HTMLController {

    @Autowired // Required to create an actual DB object instead of a java object
    private DatabaseService databaseService;

    @GetMapping("/")
    public String index(Model model) {


        model.addAttribute("itemsCount", databaseService.getItemsCount());

        return "index";
    }
}
