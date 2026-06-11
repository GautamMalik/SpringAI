package com.apring.ai.controller;


import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatMemoryController {

    @Autowired
    @Qualifier("chat")
    private ChatClient chatClient;

    @GetMapping("/chatMemory/chat")
    public String chatOpenAi(@RequestParam("message") String message, @RequestParam("userId") String userId){
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        userId))  // userId to handle diffrent user chat memory at same time and "default" for only 1 user case
                .call().content();
    }
}
