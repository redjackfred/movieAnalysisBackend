package com.peiwentang.movieAnalysis.character;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peiwentang.movieAnalysis.rating.RatingAssistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class CharacterController {
    private final String token = System.getenv("TMDB_API_TOKEN");
    private ChatLanguageModel chatLanguageModel;

    CharacterController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @RequestMapping(value = "/getCharacterRelations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCharacterRelations(@RequestParam Map<String, String> queryParameters) throws IOException {
        JsonObject outputJson = new JsonObject();
        WebClient client = WebClient.create();
        String response = null;
        String uri = "https://api.themoviedb.org/3/movie/" + queryParameters.get("movieid") + "?language=en-US";

        try{
            // Get IMDb Id and title name from TMDb api
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

            // Get cast from TMDb api
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
            Map<String, String> castImageMap = new HashMap<>();
            for(int i = 0; i < castArray.size(); i++){
                String castName = castArray.get(i).getAsJsonObject().get("character").getAsString();
                castList.add(castName);
                String castImageURL = "https://image.tmdb.org/t/p/w200";
                if(!castArray.get(i).getAsJsonObject().get("profile_path").isJsonNull()) {
                    castImageURL += castArray.get(i).getAsJsonObject().get("profile_path").getAsString();
                    castImageMap.put(castName, castImageURL);
                }
            }
            System.out.println("castList: " + castList);
            // Generate character relation from GPT4-o
            RatingAssistant ratingAssistant = AiServices.builder(RatingAssistant.class)
                    .chatLanguageModel(chatLanguageModel)
                    .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(2))
                    .build();

            String userInput = "User: I would like to generate character relations for a movie.";
            System.out.println(userInput);
            response = ratingAssistant.generateCharacterRelation(userInput);
            System.out.println(response);

            String userMessage = "Title: [" + title + "], IMDB ID: [" + imdbId + "], ";
            userMessage += castList.toString();
            System.out.println("User Message: " + userMessage);

            response = ratingAssistant.generateCharacterRelation(userMessage);
            System.out.println("AI generated: \n" + response);


            /*  Generated from GPT4-o
               {
                "Harry Potter": {
                    "Ron Weasley": "best friend",
                    "Hermione Granger": "best friend",
                    "Dobby (voice)": "helper",
                    "Rubeus Hagrid": "friend",
                    "Albus Dumbledore": "mentor",
                    "Severus Snape": "antagonist",
                    "Draco Malfoy": "rival",
                    "Ginny Weasley": "crush"
                },
                "Ron Weasley": {
                    "Harry Potter": "best friend",
                    "Hermione Granger": "friend",
                    "Severus Snape": "antagonist",
                    "Draco Malfoy": "rival"
                },
                ...
               }

               change to:
               {
                "nodes": [
                  {"id": "Harry Potter"},
                  {"id": "Ron Weasley"},
                 ],
                 "links": [
                      {"source": "Harry Potter", "target": "Ron Weasley", "value": "best friend"},
                      {"source": "Ron Weasley", "target": "Harry Potter", "value": "best friend"},
                      ...
                     ],
                }
             */
            // Prepare graph data
            obj = JsonParser.parseString(response).getAsJsonObject();
            JsonArray nodesArray = new JsonArray();
            JsonArray linksArray = new JsonArray();
            Set<String> nameSet = new HashSet<>();
            for(Map.Entry<String, JsonElement> entry : obj.entrySet()){
                JsonObject sourceNode = new JsonObject();
                sourceNode.addProperty("id", entry.getKey());
                sourceNode.addProperty("imageURL", castImageMap.get(entry.getKey()));
                if(!nameSet.contains(entry.getKey())){
                    nameSet.add(entry.getKey());
                    nodesArray.add(sourceNode);
                }
                for(Map.Entry<String, JsonElement> relation: ((JsonObject)entry.getValue()).entrySet()){
                    JsonObject targetNode = new JsonObject();
                    if(!nameSet.contains(relation.getKey())){
                        nameSet.add(relation.getKey());
                        targetNode.addProperty("id", relation.getKey());
                        targetNode.addProperty("imageURL", castImageMap.get(relation.getKey()));
                        nodesArray.add(targetNode);
                    }

                    JsonObject link = new JsonObject();
                    link.addProperty("source", entry.getKey());
                    link.addProperty("target", relation.getKey());
                    link.addProperty("value", 2);
                    link.addProperty("relation", relation.getValue().getAsString());
                    linksArray.add(link);
                }
            }
            outputJson.add("nodes", nodesArray);
            outputJson.add("links", linksArray);
            outputJson.addProperty("color", "aa15FF");

        }catch (URISyntaxException e) {
            System.out.println("error");
        }



        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("Relation.json"));
            bw.write(outputJson.toString());
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
            }
        }


        return outputJson.toString();
    }
}
