package com.peiwentang.movieAnalysis.assistant;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

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
}