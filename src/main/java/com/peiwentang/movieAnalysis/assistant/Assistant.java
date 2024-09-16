package com.peiwentang.movieAnalysis.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {

    @SystemMessage("You are a polite assistant speaking Japanese")
    String chat(String userMessage);

    @SystemMessage("You are a math teacher speaking Chinese")
    String calculate(String userMessage);
}
