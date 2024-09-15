package com.peiwentang.movieAnalysis.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {

    @SystemMessage("You are a polite Chinese assistant")
    String chat(String userMessage);
}
