package com.ai.mcpclient.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/mcpClient")
public class McpClientController {

    @Autowired
    private ChatClient chatClient;

    @Value("classpath:/promptTemplates/systemPrompt.st")
    private Resource prompt;

    // bad model executing wrong tools

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {

        return chatClient.prompt()
                .system(prompt)
                .user(message)
                .call()
                .content();
    }
}

