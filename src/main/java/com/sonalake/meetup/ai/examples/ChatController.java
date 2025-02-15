package com.sonalake.meetup.ai.examples;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatController {
    private final ChatClient chatClient;
    private final TextToSpeechController textToSpeechController;

    public ChatController(ChatClient chatClient, TextToSpeechController textToSpeechController) {
        this.chatClient = chatClient;
        this.textToSpeechController = textToSpeechController;
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

    /**
     * Handles a GET request to generate a chat response based on the provided user prompt,
     * converts the response text to speech, and plays it asynchronously.
     *
     * @param prompt the user-provided prompt or input message, defaults to "Tell me a joke" if not provided
     * @return the original chat response text generated
     */
    @GetMapping("chat/tts")
    public String chatTextToSpeech(@RequestParam(defaultValue = "Tell me a joke", name = "prompt") String prompt) {

        String text = chatClient.prompt().user(prompt).call().content();

        return textToSpeechController.textToSpeech(text);
    }
}
