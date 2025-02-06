package com.sonalake.meetup.ai.examples;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController extends Controller {

    public ChatController(ChatClient.Builder chatClientBuilder) {
        super(chatClientBuilder);
    }

    /**
     * Handles a GET request to generate a chat response based on the provided user input.
     *
     * @param chatPrompt the input message or prompt from the user; defaults to "Hi!" if not provided
     * @return the response generated by the chat client
     */
    @GetMapping("chat")
    public String chat(@RequestParam(defaultValue = "Hi!", name = "prompt") String chatPrompt) {

        return chatClient
                .prompt()
                .user(chatPrompt)
                .call()
                .content();
    }
}
