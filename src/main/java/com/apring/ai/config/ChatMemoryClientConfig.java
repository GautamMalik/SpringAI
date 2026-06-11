package com.apring.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatMemoryClientConfig {

    // more maxmessages more context go to llm -> more input tokens -> more cost -> better use RAG
    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .maxMessages(10) // only 10 rows will be saved in db
                .chatMemoryRepository(jdbcChatMemoryRepository).build();
    }

    @Bean("chat")
    public ChatClient chatClient(OllamaChatModel ollamaChatModel, ChatMemory chatMemory){

        // chat memory is in memory
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build()))
                .build();
    }
}
