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
public class PromptTemplateController {

    @Autowired
    @Qualifier("openaiChatClient")
    private ChatClient chatClient;

//    String prompt = """
//            A customer name {name} sent following message {msg}
//
//            please give response email body and signature don't give subject
//            """;

    @Value("classpath:/promptTemplates/userPrompt.st")
    private Resource prompt;

    @GetMapping("/openAi/prompt/template/chat")
    public String chatopenAi(@RequestParam("name") String name, @RequestParam("msg") String msg) {
        return chatClient
                .prompt()
                .system("you are HR U need to give response for queries")
                .user(promptUserSpec -> promptUserSpec.text(prompt).param("name",name).param("msg",msg))
                .call()
                .content();
    }
}
