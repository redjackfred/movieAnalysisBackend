package com.peiwentang.movieAnalysis.rating;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;


@AiService
public interface RatingAssistant {

    @SystemMessage(fromResource = "systemPrompt.txt")
    String chat(String userMessage);
}
