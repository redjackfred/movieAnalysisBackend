package com.peiwentang.movieAnalysis.rating;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;


@AiService
public interface RatingAssistant {

    @SystemMessage(fromResource = "systemPrompt.txt")
    String generateReport(String userMessage);

    @SystemMessage(fromResource = "systemPromptForCharacterRelation.txt")
    String generateCharacterRelation(String userMessage);

    @SystemMessage(fromResource = "systemPromptForLearningTakeaways.txt")
    String generateLearningTakeaways(String userMessage);

    @SystemMessage(fromResource = "systemPromptForStoryTimeline.txt")
    String generateStoryTimeline(String userMessage);
}
