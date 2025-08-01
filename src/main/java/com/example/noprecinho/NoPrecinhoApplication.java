package com.example.noprecinho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NoPrecinhoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoPrecinhoApplication.class, args);

        WebScraper webScraper = new WebScraper();
        String document;
        //document = webScraper.tauste_connect("saojosedoscampos", "164607-alimento-yakult-hiline-f-frasco-100ml.html");

        ContentAnalysis contentAnalysis = new ContentAnalysis();
        //contentAnalysis.tauste_price(document, "Yakult Hiline");

        //document = webScraper.carrefour_connect("complemento-alimentar-sustagen-kids-sabor-baunilha-lata-350g-8707847");

        //contentAnalysis.carrefour_price(document, "Sustagen Kids");

        document = webScraper.shibata_connect("6172/pack-barra-de-cereal-aveia-com-banana-e-mel-nutry-pacote-com-3-unidades-66g");
        contentAnalysis.shibata_price(document, "Barra de Cereal Aveia");
    }




}
