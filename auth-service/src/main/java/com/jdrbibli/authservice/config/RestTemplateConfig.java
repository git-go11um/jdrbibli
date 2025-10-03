package com.jdrbibli.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration pour les appels HTTP externes via RestTemplate.
 * 
 * Cette classe fournit un bean {@link RestTemplate} qui peut être injecté
 * dans les différents services ou composants du microservice AuthService.
 * 
 * {@link RestTemplate} permet de faire des requêtes HTTP (GET, POST, etc.)
 * vers d'autres services ou API externes.
 * 
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Crée un {@link RestTemplate} prêt à l'utilisation.
     *
     * @return un {@link RestTemplate} singleton pour l'application
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
