package com.peiwentang.movieAnalysis.rating;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peiwentang.movieAnalysis.movie.MovieManager;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


@RestController
public class RatingController {
    private final String omdbApiKey = System.getenv("OMDB_API_KEY");
    private ChatLanguageModel chatLanguageModel;

    RatingController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @RequestMapping(value = "/getPublicRatings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPublicRatings(@RequestParam Map<String, String> queryParameters) {
        WebClient client = WebClient.create();
        String response = null;
        String imdbId = queryParameters.get("imdbid");
        String uri = "https://www.omdbapi.com/?i=" + imdbId + "&apikey=" + omdbApiKey;
        try {
            response = client.get()
                    .uri(new URI(uri))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (URISyntaxException e) {
            System.out.println("error");
        }

        // Reformat json
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("Ratings");

        JsonObject outputJsonObject = new JsonObject();
        JsonArray resultJsonArray = new JsonArray();
        for(int i = 0; i < jsonArray.size(); i++){
            JsonObject outputRatings = new JsonObject();
            JsonObject rating = jsonArray.get(i).getAsJsonObject();
            String score = null;
            switch (rating.get("Source").getAsString()){
                case "Internet Movie Database":
                    outputRatings.addProperty("Source", "IMDb");
                    score = rating.get("Value").getAsString();
                    score = score.substring(0, score.indexOf("/"));
                    outputRatings.addProperty("Rating", score);
                    break;
                case "Rotten Tomatoes":
                    outputRatings.addProperty("Source", "Rotten Tomatoes");
                    score = rating.get("Value").getAsString();
                    score = score.substring(0, score.indexOf("%"));
                    outputRatings.addProperty("Rating", score);
                    break;
                case "Metacritic":
                    outputRatings.addProperty("Source", "Metacritic");
                    score = rating.get("Value").getAsString();
                    score = score.substring(0, score.indexOf("/"));
                    outputRatings.addProperty("Rating", score);
                    break;
            }
            resultJsonArray.add(outputRatings);
        }
        outputJsonObject.add("Results", resultJsonArray);

        return outputJsonObject.toString();
    }


    @RequestMapping(value = "/generateReport", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String generateReport(@RequestParam Map<String, String> queryParameters) {
        RatingAssistant ratingAssistant = AiServices.builder(RatingAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(2))
                .build();

        String userInput = "User: I would like to analyze a movie.";
        System.out.println(userInput);
        String answer = ratingAssistant.chat(userInput);
        System.out.println(answer);
        String title = queryParameters.get("title");
        String imdbId = queryParameters.get("imdbId");
        answer = ratingAssistant.chat("Title: [" + title + "], IMDB ID: [" + imdbId + "]");
        System.out.println(answer);

        return answer;
    }


}
