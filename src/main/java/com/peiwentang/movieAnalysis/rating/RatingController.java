package com.peiwentang.movieAnalysis.rating;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


// TODO: This is just a template for later development
@RestController
public class RatingController {
    ChatLanguageModel chatLanguageModel;

    RatingController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @RequestMapping(value = "/rating", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello(@RequestParam Map<String, String> queryParameters) {
        RatingAssistant ratingAssistant = AiServices.builder(RatingAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(1))
                .build();
        String answer = ratingAssistant
                .chat("give me the JSON for IMDb ID: " +
                        queryParameters.get("imdbid") +", Title: \" " + queryParameters.get("title") +  "\"");
        return answer;
    }


}
