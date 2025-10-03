package com.jdrbibli.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configuration CORS pour le gateway-service.
 * 
 * Cette classe définit les règles CORS (Cross-Origin Resource Sharing) permettant
 * au frontend Angular (http://localhost:4200) d'accéder aux endpoints du gateway.
 * 
 * Elle autorise :
 * <ul>
 *   <li>tous les headers</li>
 *   <li>tous les types de méthodes HTTP</li>
 *   <li>l'envoi de cookies et d'informations d'authentification</li>
 * </ul>
 * 
 * Les règles sont appliquées à toutes les routes (/**).
 */
@Configuration
public class CorsConfig {

    /**
     * Bean qui crée et configure le filtre CORS pour toutes les requêtes entrantes.
     *
     * @return un {@link CorsWebFilter} configuré pour le frontend Angular.
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
