package com.apring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RolesMessageController {

    @Autowired
    @Qualifier("openaiChatClient")
    private ChatClient chatClient;

    private ChatClient olamaChatClient;

    RolesMessageController(ChatClient.Builder ollamaChatClient){
        this.olamaChatClient = ollamaChatClient
                .defaultSystem("You a professional Movie Recommender") // default value
                .defaultUser("Recommend a movie") // default user msg
                .build();
    }

    @GetMapping("/openAi/Role/chat/joke")
    public String chatOpenAi(@RequestParam("message") String message) {
        return chatClient
                .prompt()
                .system("You a professional joke teller")
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/openAi/Role/chat/movie/recommend")
    public String chat(@RequestParam("message") String message) {
        return olamaChatClient
                .prompt()
                .system("You are a horror movie recommendation system") // overriding default value
//                .user(message)  commenting so it will use default user msg
                .call()
                .content();
    }
}
