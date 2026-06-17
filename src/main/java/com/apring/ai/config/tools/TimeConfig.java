package com.apring.ai.config.tools;

import com.apring.ai.advisors.TokenUsageAuditAdvisor;
import com.apring.ai.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TimeConfig {

    @Bean("timeClient")
    public ChatClient chatClient(OllamaChatModel ollamaChatModel, ChatMemory chatMemory, TimeTools timeTool){
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build(),new TokenUsageAuditAdvisor()))
                .defaultTools(timeTool)
                .build();
    }


    ///  To throw exception direct without llm rephrasing make DefaultToolExecutionExceptionProcessor true default value is false

//    @Bean
//    ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
//        return new DefaultToolExecutionExceptionProcessor(true);
//    }

}
