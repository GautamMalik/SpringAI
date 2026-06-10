package com.apring.ai.controller;

import com.apring.ai.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdvisorController {

    @Autowired
    @Qualifier("openaiChatClientWithAdvisor")
    private ChatClient chatClient;

    @Autowired
    @Qualifier("openaiChatClient")
    private ChatClient chatClientWithoutDefaultAdvisor;

    @GetMapping("/openAi/advisor/chat")
    public String chatOpenAi(@RequestParam("message") String message){
        return chatClient.prompt(message).call().content();
    }

    @GetMapping("/openAi/advisor/chat2")
    public String chatOpenAi2(@RequestParam("message") String message){
        return chatClientWithoutDefaultAdvisor.prompt(message)
                .advisors(new SimpleLoggerAdvisor(),
                        SafeGuardAdvisor.builder()
                        .sensitiveWords(List.of("sucide"))
                        .failureResponse("This topic is not allowed.")
                        .build())
                .call().content();
    }

    @GetMapping("/openAi/advisor/chat3")
    public String chatOpenAi3(@RequestParam("message") String message){
        return chatClientWithoutDefaultAdvisor.prompt()
                .system("act as a gen ai model and you have to resolve queries and give ans of problems")  // to increase tokens
                .user(message)
                .advisors(new TokenUsageAuditAdvisor())
                .call().content();
    }
}
