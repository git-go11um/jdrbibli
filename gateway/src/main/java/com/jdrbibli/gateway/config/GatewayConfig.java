/* package com.jdrbibli.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; */

/**
 * Configuration des routes pour le gateway-service.
 * 
 * Cette classe définit comment les requêtes entrantes sont routées vers les microservices
 * correspondants. Elle utilise le {@link RouteLocatorBuilder} de Spring Cloud Gateway.
 * 
 * Exemple : toutes les requêtes commençant par {@code /api/auth/**} sont redirigées
 * vers le microservice {@code auth-service} sur {@code http://localhost:8081}, 
 * en supprimant le préfixe {@code /api}.
 */
/* @Configuration
public class GatewayConfig { */

    /**
     * Crée un {@link RouteLocator} personnalisé avec les règles de routage.
     *
     * @param builder le {@link RouteLocatorBuilder} fourni par Spring Cloud Gateway.
     * @return un {@link RouteLocator} configuré pour le routage des microservices.
     */
    /* @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://auth-service:8081"))
                .build();
    }
} */
