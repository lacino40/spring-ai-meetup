package com.sonalake.meetup.ai.examples;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class MovieController {
    @Value("classpath:/prompts/movie-prompt.st")
    private Resource moviePromptResource;
    
    private final ChatClient chatClient;

    public MovieController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping( "movie")
    public Flux<String> movie(@RequestParam(name = "franchise", defaultValue = "Alien") String franchise) {
        String userMessage = new PromptTemplate(moviePromptResource).create(Map.of("franchise", franchise)).getContents();

        return chatClient
                .prompt()
                .user(userMessage)
                .stream()
                .content();

    }

    @GetMapping( "movie/json")
    public List<Movie> movieJson(@RequestParam(name = "franchise", defaultValue = "Alien") String franchise) {
        String userMessage = new PromptTemplate(moviePromptResource).create(Map.of("franchise", franchise)).getContents();

        return chatClient
                .prompt()
                .user(userMessage)
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

    public record Movie(String title, int year, String director, String description) {}
}
