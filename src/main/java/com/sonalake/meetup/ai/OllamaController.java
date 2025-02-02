package com.sonalake.meetup.ai;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequestMapping("/ollama")
public class OllamaController {

    private final ChatClient chatClient;

    //v1.3 ====
    @Value("classpath:/prompts/system-prompt.st")
    private Resource systemPromptResource;
    private Prompt systemPrompt;
    //===

    public OllamaController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostConstruct
    public void init() throws IOException {
        systemPrompt = new Prompt(systemPromptResource.getContentAsString(UTF_8));
    }

    /**
     * Demo XXXXX
     *
     * Requests:
     * - GET http://localhost:8080/ollama/joke?prompt=Tell me a joke about java meetup
     */
    //TODO pouzi pre ine ucely
    @GetMapping("joke")
    public String joke(@RequestParam(defaultValue = "Tell me a joke", name = "prompt") String jokePrompt) {
        return chatClient.prompt(jokePrompt).call().content();
    }

    /**
     * Demo 1
     *  1.1: simple message
     *  1.2: simple message + wannabe system message
     *  1.3: full impl with resource
     *
     * Requests:
     * - GET http://localhost:8080/ollama/chat?prompt=What's the weather in Kosice ?
     */
    @GetMapping("chat")
    public String chat(@RequestParam(defaultValue = "Hi!", name = "prompt") String chatPrompt) {
        //v1: simple request with system wannabe message
//        String message = "If you don't know, replay with 'I don't know.'";
//        return chatClient.prompt(chatPrompt+" "+message).call().content();

        //v2: request with system and user prompt
//       Prompt systemPrompt  = new Prompt("If you don't know, replay with 'I don't know.'");
//       return chatClient.prompt(systemPrompt).user(chatPrompt).call().content();

       //v3: request with system and user prompt using resources *.st
        return chatClient.prompt(systemPrompt).user(chatPrompt).call().content();
    }


}