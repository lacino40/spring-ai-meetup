package com.sonalake.meetup.ai.service;


import java.util.Map;

public interface WeatherService {
    OpenWeatherDto.Forecast getWeather(Map<String, Object> model);
}