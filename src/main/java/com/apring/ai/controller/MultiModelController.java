package com.apring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MultiModelController {

    private ChatClient openaiChatClient;
    private ChatClient ollamaChatClient;

    MultiModelController(@Qualifier("openaiChatClient") ChatClient openaiChatClient,
                         @Qualifier("ollamaChatClient") ChatClient ollamaChatClient){
        this.openaiChatClient = openaiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("ollama/chat")
    public String chatollama(@RequestParam("message") String message){
        return ollamaChatClient.prompt(message).call().content();
    }

    @GetMapping("openAi/chat")
    public String chatOpenAi(@RequestParam("message") String message){
        return openaiChatClient.prompt(message).call().content();
    }
}
