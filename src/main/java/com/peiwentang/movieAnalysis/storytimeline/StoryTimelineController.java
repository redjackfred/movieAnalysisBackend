package com.peiwentang.movieAnalysis.storytimeline;

import com.peiwentang.movieAnalysis.rating.RatingAssistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StoryTimelineController {
    private final String omdbApiKey = System.getenv("OMDB_API_KEY");
    private ChatLanguageModel chatLanguageModel;

    StoryTimelineController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @RequestMapping(value = "/getStoryTimeline", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPublicRatings(@RequestParam Map<String, String> queryParameters) {
        RatingAssistant assistant = AiServices.builder(RatingAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(2))
                .build();

        String userMessage = "User: I would like to generate a story timeline.";
        System.out.println(userMessage);
        String answer = assistant.generateStoryTimeline(userMessage);
        System.out.println(answer);

        // Prepare user message
        String title = queryParameters.get("title");
        String imdbId = queryParameters.get("imdbid");
        userMessage = "Title: [" + title + "], IMDB ID: [" + imdbId + "]";

        answer = assistant.generateStoryTimeline(userMessage);


        return answer;
    }
}