package com.peiwentang.movieAnalysis.assistant;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is an example of using an {@link AiService}, a high-level LangChain4j API.
 */
@RestController
class AssistantController {
    ChatLanguageModel chatLanguageModel;

    AssistantController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @GetMapping("/assistant")
    public String genralChat(@RequestParam(value = "message", defaultValue = "What is the time now?") String message) {
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(new AssistantTool())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistant.chat(message);
    }

    @GetMapping("/assistant/calculate")
    public String calculate() {
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(new AssistantTool())
                .build();

        String answer = assistant.calculate("What is (1+2) * (3*4)?");
        return answer;
    }

}
