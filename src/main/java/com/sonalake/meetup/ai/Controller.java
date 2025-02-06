package com.sonalake.meetup.ai;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequestMapping("/ai")
public abstract class Controller {
    @Value("classpath:/prompts/system-prompt.st")
    private Resource systemPromptResource;

    Prompt systemPrompt;

    /**
     * Initializes the systemPrompt field by reading the content of the resource
     * specified by the systemPromptResource property.
     *
     * @throws IOException if an I/O error occurs while reading the resource content
     */
    @PostConstruct
    public void init() throws IOException {
        systemPrompt = new Prompt(systemPromptResource.getContentAsString(UTF_8));
    }
}
