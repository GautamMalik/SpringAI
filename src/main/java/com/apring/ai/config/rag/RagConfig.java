package com.apring.ai.config.rag;

import com.apring.ai.advisors.TokenUsageAuditAdvisor;
import com.apring.ai.rag.PIIMaskingDocumentPostProcessor;
import com.apring.ai.rag.WebSearchDocumentRetriever;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class RagConfig {

    // VectorStoreDocumentRetriever implements DocumentRetriever ->
    // if we want to have our own DocumentRetriever then we can implement this class and provide its implementation
    // -> implementation for enabling web for llm -> in custom class retrive method will cal web to get result
    // implemented WebSearchDocumentRetriever for same

    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore, OllamaChatModel ollamaChatModel) {
        return RetrievalAugmentationAdvisor.builder()
//                .queryTransformers(RewriteQueryTransformer.builder().chatClientBuilder().build())
                .queryTransformers(query -> getQuery(ollamaChatModel, query)) // Pre Transform -> this is custom as inbuilt was not working
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore).topK(3).similarityThreshold(0.5).build())
                .documentPostProcessors(PIIMaskingDocumentPostProcessor.builder())  // Post Transform -> after data retrival from db process data
                .build();
    }

    @NotNull
    private static Query getQuery(OllamaChatModel ollamaChatModel, Query query) {
        System.out.println("Before = " + query.text());

        ChatResponse response = ollamaChatModel.call(
                new Prompt("""
                Rewrite the following search query for vector retrieval.
                Return ONLY the rewritten query.

                %s
                """.formatted(query.text()))
        );

        String rewrittenQuery =
                response.getResult().getOutput().getText();

        System.out.println("After = " + rewrittenQuery);

        return Query.builder()
                .text(rewrittenQuery)
                .history(query.history())
                .context(query.context())
                .build();
    }

    @Bean
    @Primary
    ChatClient.Builder ragTransformerBuilder(OllamaChatModel model) {
        System.out.println(model.getOptions().getModel());
        return ChatClient.builder(model);
    }


    @Bean("chatClientWithRagAndMemoryAdvisor")
    public ChatClient chatClient(OllamaChatModel ollamaChatModel, ChatMemory chatMemory, RetrievalAugmentationAdvisor retrievalAugmentationAdvisor){
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(List.of(MessageChatMemoryAdvisor.builder(chatMemory).build(),retrievalAugmentationAdvisor, new SimpleLoggerAdvisor(),new TokenUsageAuditAdvisor()))
                .build();
    }


    // Bean for custom DocumentRetriever

    // instead of using  RetrievalAugmentationAdvisor can implement custom also like RagController::chat()
    @Bean("webSearchRAGChatClient")
    public ChatClient chatClientWeb(ChatClient.Builder chatClientBuilder,
                                 ChatMemory chatMemory, RestClient.Builder restClientBuilder) {
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        var webSearchRAGAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(WebSearchDocumentRetriever.builder().restClientBuilder(restClientBuilder).maxResults(5).build())
                .build();

        return chatClientBuilder
                .defaultAdvisors(List.of(loggerAdvisor, memoryAdvisor, tokenUsageAdvisor, webSearchRAGAdvisor))
                .build();
    }
}
