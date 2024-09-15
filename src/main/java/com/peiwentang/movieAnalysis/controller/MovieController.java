package com.peiwentang.movieAnalysis.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@RestController
public class MovieController {
    @Value("${TMDB_API_TOKEN}")
    private String token;

    @RequestMapping(value = "/movies", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
    public String getMovie(@RequestParam Map<String, String> queryParameters){
        WebClient client = WebClient.create();
        String response = null;

        try {
            response = client.get()
                    .uri(new URI("https://api.themoviedb.org/3/movie/" + queryParameters.get("movieid") + "?language=en-US"))
                    .header("Authorization", "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
            System.out.println(obj.get("genres").getAsJsonArray().toString());
        } catch (URISyntaxException e) {
            System.out.println("error");
        }
        return response;
    }


}
