package com.apring.ai.controller;

import com.apring.ai.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RagController {

    @Autowired
    @Qualifier("chat")
    private ChatClient chatMemoryClient;

    @Autowired
    private VectorStore vectorStore;

    @Value("classpath:/promptTemplates/systemPromptRandomDataTemplate.st")
    private Resource prompt;

    /// Static source for RAG limited info  -> vector db stores by RandomDataLoaderToVectorDB
    // still giving response as bad model but prompt and documents are going in system prompt
    @GetMapping("/rag/chat")
    public String chat(@RequestParam("message") String message, @RequestParam("userId") String userId){

        SearchRequest request = SearchRequest.builder().query(message).topK(3).similarityThreshold(0.5).build();

        List<Document> similarDocuments = vectorStore.similaritySearch(request);
        String similarContext = similarDocuments.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));

        System.out.println("context -> "  + similarContext);
        return chatMemoryClient.prompt()
                .system(promptSystemSpec ->  promptSystemSpec.text(prompt).param("documents",similarContext))
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        userId))
                .advisors(new TokenUsageAuditAdvisor())
                .user(message)
                .call().content();
    }


    /// Dynamic source for RAG -> PDFs -> vector db stores by ThreeStoriesPdfLoader
    // can use below one for smaller cases use RAG advisor if want customization then use above one
    @Autowired
    @Qualifier("chatClientWithRagAndMemoryAdvisor")
    private ChatClient chatClient;

    @GetMapping("/rag/chat2")
    public ResponseEntity<String> chat2(@RequestParam("message") String message, @RequestParam("userId") String userId){
        String answer = chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .user(message)
                .call().content();
        return ResponseEntity.ok(answer);
    }


    /// WEB as source for RAG -> vector db doesn't store anything
    // will not work as account not created on TAVLEY
    @Autowired
    @Qualifier("webSearchRAGChatClient")
    private ChatClient chatClientWeb;

    @GetMapping("/rag/chat3")
    public String chat3(@RequestParam("message") String message, @RequestParam("userId") String userId){
        return chatClientWeb.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .user(message)
                .call().content();
    }

}
