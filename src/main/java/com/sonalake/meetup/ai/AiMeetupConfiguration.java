package com.sonalake.meetup.ai;

import com.sonalake.meetup.ai.service.WeatherProperties;
import com.sonalake.meetup.ai.service.WeatherService;
import com.sonalake.meetup.ai.service.WeatherServiceImpl;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class AiMeetupConfiguration {
    @Value("classpath:/context/theory-of-AiMeetup-relativity.txt")
    public Resource ragTxtContext;

    @Value("vector-store.json")
    private String vectorStoreName;

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
    @Description("Get the current weather forecast for the given city")
    public WeatherService weatherService(WeatherProperties weatherProperties) {
        return new WeatherServiceImpl(weatherProperties);
    }

}
