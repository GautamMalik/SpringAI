package com.apring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class StreamController {

    @Autowired
    @Qualifier("openaiChatClient")
    private ChatClient chatClient;


    // will work in browser not in postman
    @GetMapping("/openAi/stream/chat")
    public Flux<String> chatOpenAi(@RequestParam("message") String message) {
        return chatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }
}
