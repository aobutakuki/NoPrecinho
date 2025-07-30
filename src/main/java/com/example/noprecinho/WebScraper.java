package com.example.noprecinho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WebScraper {

    public String connect(String region, String item) {
        URL base_tausteURL;
        String document;
        {
            try {
                base_tausteURL = new URL("https://tauste.com.br/" + region + "/" + item);

                System.out.println("Attempting connection to: " + base_tausteURL);

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        HttpURLConnection tauste_connection = null;
        try {
            tauste_connection = (HttpURLConnection) base_tausteURL.openConnection();
            tauste_connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(tauste_connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            document = builder.toString();

            return document;


        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tauste_connection != null) {
                tauste_connection.disconnect();
            }
        }
    }
}
