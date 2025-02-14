package com.sonalake.meetup.ai.examples;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient
                .mutate()
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    /**
     * Handles a GET request to generate a chat response based on the provided user prompt.
     *
     * @param prompt the user-provided prompt or input message, defaults to "Hi!" if not provided
     * @return a Flux stream of strings containing the chat response
     */
    @GetMapping("chat")
    public Flux<String> chat(@RequestParam(defaultValue = "Hi!", name = "prompt") String prompt) {

        return chatClient
                .prompt()
                .user(prompt)
                .stream()
                .content();
    }
}
