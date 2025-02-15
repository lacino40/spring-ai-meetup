package com.sonalake.meetup.ai.examples;

import com.sonalake.meetup.ai.service.WeatherService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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

        ToolCallback weatherCallback = FunctionToolCallback
                .builder("currentWeather", weatherService)
                .description("Fetches the current weather for a specified city. " +
                             "Requires a 'city' parameter, which should be passed as a string representing the name of the city.")
                .inputType(WeatherService.CityForecastRequest.class)
                .build();

        String userMessage = new PromptTemplate(weatherPromptResource).create(model).getContents();

        return chatClient
                .prompt()
                .user(userMessage)
                .tools(weatherCallback)
                .call()
                .content();
    }
}
