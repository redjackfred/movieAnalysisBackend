package com.peiwentang.movieAnalysis.takeaways;

import com.peiwentang.movieAnalysis.rating.RatingAssistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class TakeawaysController {
    private final String token = System.getenv("TMDB_API_TOKEN");
    private ChatLanguageModel chatLanguageModel;

    TakeawaysController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @RequestMapping(value = "/generateLearningTakeaways", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCharacterRelations(@RequestParam Map<String, String> queryParameters) throws IOException {
        RatingAssistant ratingAssistant = AiServices.builder(RatingAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(2))
                .build();

        String userInput = "User: I would like to analyze a movie.";
        System.out.println(userInput);
        String answer = ratingAssistant.generateLearningTakeaways(userInput);
        System.out.println(answer);

        // Prepare user message
        String title = queryParameters.get("title");
        String imdbId = queryParameters.get("imdbid");
        String userMessage = "Title: [" + title + "], IMDB ID: [" + imdbId + "]";
        answer = ratingAssistant.generateLearningTakeaways(userMessage);

        System.out.println(answer);

        return answer;
    }

}