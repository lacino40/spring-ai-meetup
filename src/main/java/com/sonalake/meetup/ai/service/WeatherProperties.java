package com.sonalake.meetup.ai.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.file.Paths;

import static org.apache.commons.lang.StringUtils.isBlank;

@Data
@ConfigurationProperties("service.weather.open-weather")
public class WeatherProperties {
    private String url;
    private String version;
    private String unit;
    private String apiKey;
    private String mockFilePath;

    public URI getOpenWeatherUrl(String query) {

        return isBlank(apiKey) ?

                //build URI for open-weather mock file
                Paths.get(mockFilePath).toUri() :

                //build URI for open-weather API
                UriComponentsBuilder
                        .fromUriString(url)
                        .pathSegment(version, "weather")
                        .queryParam("q", query)
                        .queryParam("units", unit)
                        .queryParam("APPID", apiKey)
                        .build()
                        .toUri();
    }

    public boolean isMocked() {
        return isBlank(apiKey);
    }
}
