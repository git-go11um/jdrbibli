package com.jdrbibli.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration pour les appels HTTP réactifs via WebClient.
 * 
 * Cette classe fournit un bean {@link WebClient} qui peut être injecté
 * dans les services ou composants nécessitant des appels HTTP non bloquants
 * vers d'autres services ou API externes.
 * 
 * 
 * {@link WebClient} est recommandé pour les communications réactives et
 * permet de gérer les flux de données de manière asynchrone.
 * 
 */
@Configuration
public class WebClientConfig {

    /**
     * Crée un {@link WebClient} prêt à l'utilisation.
     *
     * @return un {@link WebClient} singleton pour l'application
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://user-service:8082/api/users")
                .build();
    }
}
