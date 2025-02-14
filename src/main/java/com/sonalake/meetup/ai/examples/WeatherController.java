package com.sonalake.meetup.ai.examples;

import com.sonalake.meetup.ai.service.WeatherService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.ai.ollama.api.OllamaModel.LLAMA3_2;

@RestController
@RequestMapping("/ai")
public class WeatherController {
    private final ChatClient chatClient;
    private final WeatherService weatherService;

    @Value( "classpath:/prompts/weather-prompt.st")
    private Resource weatherPromptResource;

    public WeatherController(ChatClient chatClient, WeatherService weatherService) {
        this.chatClient = chatClient;
        this.weatherService = weatherService;
    }

    @GetMapping("forecast")
    public String forecast(@RequestParam(defaultValue = "London", name = "city") String city) {
        Map<String, Object> model = Map.of("city", city);
        OllamaOptions ollamaOptions = OllamaOptions.builder().model(LLAMA3_2).build();

        FunctionCallback weatherCallback = FunctionCallback.builder()
                .function("getWeather", weatherService::getWeather)
                .description("Fetches the current weather for a specified city. " +
                             "Requires a 'city' parameter, which should be passed as a string representing the name of the city. " +
                             "Input should be a Map with the key 'city'.")
                .inputType(Map.class)
                .build();

        Prompt prompt = new PromptTemplate(weatherPromptResource).create(model, ollamaOptions);

        return chatClient
                .prompt(prompt)
                .functions(weatherCallback)
                .call()
                .content();
    }
}
