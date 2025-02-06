package com.sonalake.meetup.ai.examples;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/ai")
public abstract class Controller {
    private final ChatClient.Builder chatClientBuilder;
    protected ChatClient chatClient;

    @Value("classpath:/prompts/system-prompt.st")
    protected Resource systemPromptResource;

    public Controller(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    @PostConstruct
    public void init() {
        chatClient = chatClientBuilder.defaultSystem(systemPromptResource).build();
    }
}
