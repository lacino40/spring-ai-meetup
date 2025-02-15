package com.sonalake.meetup.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class WeatherService implements Function<WeatherService.CityForecastRequest, OpenWeatherDto.Forecast> {
    private static final Logger LOG = Logger.getLogger(WeatherService.class.getName());

    private final WeatherProperties weatherProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public WeatherService(WeatherProperties weatherProperties, RestTemplate restTemplate) {
        this.weatherProperties = weatherProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public OpenWeatherDto.Forecast apply(CityForecastRequest request) {
        try {
            String city = request.city();
            requireNonNull(city);

            URI openWeatherURI = weatherProperties.getOpenWeatherUrl(city);
            boolean isMocked = weatherProperties.isMocked();

            OpenWeatherDto openWeatherDto =  isMocked ?
                    mapper.readValue(Paths.get(openWeatherURI).toFile(), OpenWeatherDto.class) :
                    restTemplate.getForObject(openWeatherURI, OpenWeatherDto.class);

            requireNonNull(openWeatherDto);
            openWeatherDto.setName(city);

            OpenWeatherDto.Forecast forecast = openWeatherDto.forecast();

            LOG.info(format("Got %s weather for %s : %s", (isMocked ? "mocked" : ""), city, mapper.writeValueAsString(forecast)));

            return forecast;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public record CityForecastRequest(String city) {}
}
