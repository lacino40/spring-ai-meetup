package com.sonalake.meetup.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class WeatherServiceImpl implements WeatherService {
    private static final Logger LOG = Logger.getLogger(WeatherServiceImpl.class.getName());

    private final WeatherProperties weatherProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public WeatherServiceImpl(WeatherProperties weatherProperties, RestTemplate restTemplate) {
        this.weatherProperties = weatherProperties;
        this.restTemplate = restTemplate;
    }

    public OpenWeatherDto.Forecast getWeather(Map<String, Object> model) {
        try {
            var city = (String) model.get("city");
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
}
