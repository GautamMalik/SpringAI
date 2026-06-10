package com.apring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PromptStuffingController {

    @Autowired
    @Qualifier("openaiChatClient")
    private ChatClient chatClient;

    @Value("classpath:/promptTemplates/systemPrompt.st")
    private Resource prompt;

    @GetMapping("/openAi/prompt/stuffing/chat")
    public String chatopenAi(@RequestParam("msg") String msg) {
        return chatClient
                .prompt()
                .system(prompt)  // prompt static that's why didn't used promptSpec
                .user(msg)
                .call()
                .content();
    }
}
