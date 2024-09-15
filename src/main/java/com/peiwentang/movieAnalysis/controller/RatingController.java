package com.peiwentang.movieAnalysis.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
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
    private final String apiKey = System.getenv("OPENAI_API_KEY");

    @RequestMapping("/ratings")
    public String hello() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();
        String answer = model.generate("Say 'Hello World'");
        System.out.println(answer); // Hello World

        return "Hello World";
    }


}
