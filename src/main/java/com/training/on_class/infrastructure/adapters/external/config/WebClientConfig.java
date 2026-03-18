package com.training.on_class.infrastructure.adapters.external.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${microservices.technology.url}")
    private String technologyServiceUrl;

    @Bean
    public WebClient technologyWebClient() {
        return WebClient.builder()
          .baseUrl(technologyServiceUrl)
          .build();
    }
}