package com.sonalake.meetup.ai.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

import static java.lang.Math.round;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherDto {
    private List<Weather> weather;
    private Main main;
    private Wind wind;
    private String name;
    private Long dt;
    private String displayTime;
    private String visibility;
    private boolean mock;
    private boolean error;

    public Forecast forecast() {
        return new Forecast(
                name,
                getDisplayWeather().getDescription(),
                round(Double.parseDouble(main.getTemp()))+"°C",
                round(Double.parseDouble(main.getFeels_like()))+"°C",
                round(Double.parseDouble(main.getHumidity()))+"%",
                round(Double.parseDouble(main.getPressure()))+"hPa",
                round(Double.parseDouble(wind.getSpeed()))+"m/s"
        );

    }

    /**
     * Retrieves the very first weather object to display from the list of weather objects.
     *
     * @return the weather object to display, or null if the list is empty
     */
    public Weather getDisplayWeather() {
        return  weather.stream()
                .findFirst()
                .orElse(null);
    }

    @AllArgsConstructor
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Forecast {
        private String city;
        private String description;
        private String temperature;
        private String feelsLike;
        private String humidity;
        private String pressure;
        private String windSpeed;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private Long id;
        private String main;
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Main {
        private String temp;
        private String feels_like;
        private String pressure;
        private String humidity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Wind {
        private String speed;
    }
}
