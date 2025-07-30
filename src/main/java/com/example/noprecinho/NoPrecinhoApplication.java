package com.example.noprecinho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NoPrecinhoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoPrecinhoApplication.class, args);

        WebScraper webScraper = new WebScraper();
        webScraper.connect("saojosedoscampos", "164607-alimento-yakult-hiline-f-frasco-100ml.html");
    }




}
