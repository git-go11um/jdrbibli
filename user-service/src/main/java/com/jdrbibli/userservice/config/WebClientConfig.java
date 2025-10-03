package com.jdrbibli.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration de {@link WebClient} pour les appels HTTP réactifs.
 * 
 * Ce bean configure un {@link WebClient} avec une URL de base pointant
 * vers le microservice Gateway (http://localhost:8084). Il peut être injecté
 * dans les services pour effectuer des requêtes HTTP vers d'autres microservices.
 */
@Configuration
public class WebClientConfig {

    /**
     * Crée et configure le {@link WebClient} utilisé pour les appels HTTP.
     *
     * @return une instance de {@link WebClient} avec l'URL de base configurée
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8084")
                .build();
    }
}
