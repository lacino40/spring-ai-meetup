package com.sonalake.meetup.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class MeetupConfiguration {
    @Value("classpath:/prompts/system-prompt.st")
    public Resource systemPromptResource;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return  chatClientBuilder.defaultSystem(systemPromptResource).build();
    }
}
