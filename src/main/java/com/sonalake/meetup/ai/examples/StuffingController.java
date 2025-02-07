package com.sonalake.meetup.ai.examples;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;

@RestController
@RequestMapping("/ai")
public class StuffingController {
    private final ChatClient chatClient;

    @Value("classpath:/prompts/stuffing-prompt.st")
    private Resource stuffingPromptResource;

    @Value("classpath:/context/stuffing-context.st")
    private Resource stuffingContextResource;

    public StuffingController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Handles a GET request to generate a response based on the provided question and optional context.
     *
     * @param stuffingEnabled a flag indicating whether additional context should be included in the model,
     *                        defaults to false if not provided
     * @param questionPrompt  the user-provided question or input prompt
     * @return the response generated by the chat client
     */
    @GetMapping("stuffing")
    public String stuffing(@RequestParam(name = "stuff", defaultValue = "false", required = false) boolean stuffingEnabled,
                           @RequestParam(name = "question", defaultValue = "Hi!") String questionPrompt) {

        Map<String, Object> model = getModel(questionPrompt, stuffingEnabled);
        String userPrompt = new PromptTemplate(stuffingPromptResource).create(model).getContents();

        return chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();
    }

    /**
     * Constructs a model represented as a map containing key-value pairs. The model is pre-filled
     * with default values, and additional values are conditionally added based on the provided
     * parameters.
     *
     * @param questionPrompt the question or prompt provided by the user, to be added to the model
     *                       if stuffing is enabled
     * @param stuffingEnabled a boolean flag indicating whether additional context should be
     *                        included in the model
     * @return a map representing the constructed model, containing default values and optionally
     *         the provided question and context
     */
    private Map<String, Object> getModel(String questionPrompt, boolean stuffingEnabled) {
        Map<String, Object> model = getDefaultModel();

        if(stuffingEnabled) {
            model.put("question", questionPrompt);
            model.put("context", stuffingContextResource);
        }

        return model;
    }

    /**
     * Creates and returns a default model represented as a map with pre-defined
     * key-value pairs for "question" and "context", both initialized to empty strings.
     *
     * @return a map containing default values for the "question" and "context" keys
     */
    private Map<String, Object> getDefaultModel() {
        Map<String, Object> defaultModel = new HashMap<>();
        defaultModel.put("question", EMPTY_STRING);
        defaultModel.put("context", EMPTY_STRING);
        return defaultModel;
    }
}
