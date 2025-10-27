package com.jdrbibli.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // Utilisé pour les appels internes au gateway ou à d'autres services
        return WebClient.builder()
                .baseUrl("http://gateway:8084")
                .build();
    }

    @Bean(name = "authWebClient")
    public WebClient authWebClient() {
        // Utilisé pour appeler directement auth-service (Docker)
        return WebClient.builder()
                .baseUrl("http://auth-service:8081")
                .build();
    }
}
