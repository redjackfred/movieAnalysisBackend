package com.peiwentang.movieAnalysis.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.google.gson.*;

import java.net.URI;
import java.net.URISyntaxException;

// TODO: This is just a template for later development
@RestController
public class RatingController {
    @Value("${TMDB_API_TOKEN}")
    private String token;

    @RequestMapping("/ratings")
    public String hello() {
        WebClient client = WebClient.create();
        try {
            String response = client.get()
                    .uri(new URI("https://api.themoviedb.org/3/movie/672?language=en-US"))
                    .header("Authorization", "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
            System.out.println(obj.get("adult").getAsBoolean());
        } catch (URISyntaxException e) {
            System.out.println("error");
        }
        return "Hello World";
    }


}
