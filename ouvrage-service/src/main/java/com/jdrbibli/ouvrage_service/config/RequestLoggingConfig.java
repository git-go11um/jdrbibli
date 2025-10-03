package com.jdrbibli.ouvrage_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Configuration du logging des requêtes HTTP pour le microservice {@code ouvrage-service}.
 * <p>
 * Cette classe crée un {@link CommonsRequestLoggingFilter} pour enregistrer dans les logs :
 * <ul>
 *   <li>Les informations du client (adresse IP, session, etc.)</li>
 *   <li>La query string de la requête</li>
 *   <li>Le corps de la requête (payload) jusqu'à 10 000 caractères</li>
 * </ul>
 * <p>
 * Les headers HTTP ne sont pas inclus dans les logs pour éviter d'exposer des informations sensibles.
 * Ce filtre est utile pour le debug et le suivi des requêtes entrantes.
 */
@Configuration
public class RequestLoggingConfig {

    /**
     * Crée et configure un {@link CommonsRequestLoggingFilter} pour le microservice.
     *
     * @return le filtre de logging des requêtes HTTP.
     */
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        
        return filter;
    }
}
