package com.apring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/option")
public class ChatOptionController {

    @Autowired
    @Qualifier("openaiChatClientWithChatOption")
    private ChatClient openAiChatClient;

    @Autowired
    @Qualifier("ollamaChatClient")
    private ChatClient ollamaChatClient;

    @GetMapping("/ollama/chat")
    public String chatollama(@RequestParam("message") String message){
        return ollamaChatClient.prompt(message)
                .advisors(new SimpleLoggerAdvisor())
                .options(OllamaChatOptions.builder().minP(0.1).disableThinking())
                .call().content();
    }

    @GetMapping("/openAi/chat")
    public String chatOpenAi(@RequestParam("message") String message){
        return openAiChatClient.prompt(message).call().content();
    }
}
