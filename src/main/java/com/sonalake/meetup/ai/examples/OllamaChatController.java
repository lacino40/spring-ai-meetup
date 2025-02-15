package com.sonalake.meetup.ai.examples;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai/ollama")
public class OllamaChatController {
    private final ChatClient chatClient;

    public OllamaChatController(@Qualifier("ollamaChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Handles a GET request to generate a chat response based on the provided user prompt.
     *
     * @param prompt the user-provided prompt or input message, defaults to "Tell me an interesting fact about Ollama" if not provided
     * @return a Flux stream of strings containing the chat response
     */
    @GetMapping("chat")
    public Flux<String> chat(@RequestParam(defaultValue = "Tell me an interesting fact about Ollama", name = "prompt") String prompt) {

        return chatClient
                .prompt()
                .user(prompt)
                .stream()
                .content();
    }
}
