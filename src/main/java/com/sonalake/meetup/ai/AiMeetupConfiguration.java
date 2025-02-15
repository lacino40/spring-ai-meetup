package com.sonalake.meetup.ai;

import com.sonalake.meetup.ai.service.WeatherProperties;
import com.sonalake.meetup.ai.service.WeatherService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class AiMeetupConfiguration {
    @Value("classpath:/prompts/system-prompt.st")
    public Resource systemPromptResource;

    @Value("classpath:/context/theory-of-AiMeetup-relativity.txt")
    public Resource ragTxtContext;

    @Value("vector-store.json")
    private String vectorStoreName;

    @Bean
    @ConditionalOnProperty(name = "spring.ai.chat.client.enabled", havingValue = "true")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return  chatClientBuilder
                .defaultSystem(systemPromptResource)
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File vectorStoreFile = Paths.get(vectorStoreName).toFile();

        if(vectorStoreFile.exists()) {
            simpleVectorStore.load(vectorStoreFile);
            return simpleVectorStore;
        }

        /*
            smaller, focused chunks of text allow better query matching, ensuring that only the most relevant
            parts of the text are retrieved, rather than an entire large document
         */
        TextReader textReader = new TextReader(ragTxtContext);
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(50, 100, 50, 100, true);
        List<Document> documents = tokenTextSplitter.apply(textReader.get());

        simpleVectorStore.add(documents);
        simpleVectorStore.save(vectorStoreFile);

        return simpleVectorStore;
    }

    @Bean
    public WeatherService weatherService(WeatherProperties weatherProperties, RestTemplate restTemplate) {
        return new WeatherService(weatherProperties, restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
