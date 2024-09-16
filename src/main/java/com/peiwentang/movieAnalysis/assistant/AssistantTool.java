package com.peiwentang.movieAnalysis.assistant;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

// TODO: (Experimental) Maybe we can add external api calls here in order for Assistant to call it before generating answers by AI.

@Component
class AssistantTool {

    /**
     * This tool is available to {@link Assistant}
     */
    @Tool
    String currentTime() {
        return LocalTime.now().toString();
    }

    @Tool
    String generateSecret(){
        return "!@#$%^&*";
    }

    @Tool
    int add(int a, int b){
        return a + b;
    }

    @Tool
    int multiply(int a, int b){
        return a * b;
    }
}