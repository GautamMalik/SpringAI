package com.ai.mcpclient.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel, ToolCallbackProvider toolCallbackProvider){
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }

    @Bean
    ApplicationRunner logTools(ToolCallbackProvider provider) {
        return args -> {

            ToolCallback[] tools = provider.getToolCallbacks();

            System.out.println("=== Exposed Tools ===");

            for (ToolCallback tool : tools) {
                System.out.println("Name: " + tool.getToolDefinition().name());
                System.out.println("Description: " + tool.getToolDefinition().description());
                System.out.println("---------------------");
            }
        };
    }
}
