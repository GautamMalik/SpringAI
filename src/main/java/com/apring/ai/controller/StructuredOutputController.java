package com.apring.ai.controller;

import com.apring.ai.models.CountryCities;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StructuredOutputController {

    @Autowired
    @Qualifier("openaiChatClientWithAdvisor")
    private ChatClient chatClientWithoutDefaultAdvisor;

    @GetMapping("/openAi/structuredOutput/chat")
    public ResponseEntity<CountryCities> chatOpenAi(@RequestParam("message") String message){
        CountryCities countryCities = chatClientWithoutDefaultAdvisor.prompt(message).call().entity(CountryCities.class);
        // or
        CountryCities countryCities2 = chatClientWithoutDefaultAdvisor.prompt(message).call().entity(new BeanOutputConverter<>(CountryCities.class));

        return ResponseEntity.ok(countryCities2);
    }


    @GetMapping("/openAi/structuredOutput/chat2")
    public ResponseEntity<List<String>> chatOpenAi2(@RequestParam("message") String message){
        List<String> cities = chatClientWithoutDefaultAdvisor.prompt(message).call().entity(new ListOutputConverter());
        return ResponseEntity.ok(cities);
    }

    // sometimes getting 500 as model returning invalid json response -> better use pojo(1st api) or better model
    @GetMapping("/openAi/structuredOutput/chat3")
    public ResponseEntity<Map<String,Object>> chatOpenAi3(@RequestParam("message") String message){
        Map<String, Object> cities = chatClientWithoutDefaultAdvisor.prompt(message).call().entity(new MapOutputConverter());
        return ResponseEntity.ok(cities);
    }


    // local model giving reponse in wrong format hence 500
    @GetMapping("/openAi/structuredOutput/chat4")
    public ResponseEntity<List<CountryCities>> chatOpenAi4(@RequestParam("message") String message){
        List<CountryCities> countryCities = chatClientWithoutDefaultAdvisor.prompt(message).call().entity(new ParameterizedTypeReference<List<CountryCities>>() {});
        return ResponseEntity.ok(countryCities);
    }
}
