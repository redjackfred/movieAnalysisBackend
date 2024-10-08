package com.peiwentang.movieAnalysis.character;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peiwentang.movieAnalysis.rating.RatingAssistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class CharacterController {
    private final String token = System.getenv("TMDB_API_TOKEN");
    private ChatLanguageModel chatLanguageModel;

    CharacterController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @RequestMapping(value = "/getCharacterRelations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCharacterRelations(@RequestParam Map<String, String> queryParameters) {
        WebClient client = WebClient.create();
        String response = null;
        String uri = "https://api.themoviedb.org/3/movie/" + queryParameters.get("movieid") + "?language=en-US";

        try{
            response = client.get()
                    .uri(new URI(uri))
                    .header("Authorization", "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
            String imdbId = obj.get("imdb_id").getAsString();
            String title = obj.get("original_title").getAsString();

            uri = "https://api.themoviedb.org/3/movie/" + queryParameters.get("movieid") + "/credits?language=en-US";
            response = client.get()
                    .uri(new URI(uri))
                    .header("Authorization", "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            obj = JsonParser.parseString(response).getAsJsonObject();
            JsonArray castArray = obj.get("cast").getAsJsonArray();
            List<String> castList = new ArrayList<>();
            for(int i = 0; i < castArray.size(); i++){
                castList.add(castArray.get(i).getAsJsonObject().get("character").getAsString());
            }

            // Generate relationship from OpenAI
            RatingAssistant ratingAssistant = AiServices.builder(RatingAssistant.class)
                    .chatLanguageModel(chatLanguageModel)
                    .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(2))
                    .build();

            String userInput = "User: I would like to generate character relations for a movie.";
            System.out.println(userInput);
            response = ratingAssistant.generateCharacterRelation(userInput);
            System.out.println(response);

            String userMessage = "Title: [" + title + "], IMDB ID: [" + imdbId + "], ";
            // TODO:　Modify it!!!
            userMessage += castList.toString();
            System.out.println("User Message: " + userMessage);

            response = ratingAssistant.generateCharacterRelation(userMessage);
            System.out.println(response);


        }catch (URISyntaxException e) {
            System.out.println("error");
        }

        return response;
    }
}
