package com.sonalake.meetup;

import com.sonalake.meetup.ai.service.WeatherProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WeatherProperties.class)
public class SpringAiMeetupApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiMeetupApplication.class, args);
	}

}
