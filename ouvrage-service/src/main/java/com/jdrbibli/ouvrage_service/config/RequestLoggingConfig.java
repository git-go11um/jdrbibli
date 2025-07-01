package com.jdrbibli.ouvrage_service.config; // adapte selon ton projet

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        
        filter.setIncludeClientInfo(true);       // Affiche l'IP, le sessionId, etc.
        filter.setIncludeQueryString(true);      // Affiche la chaîne de requête (query string)
        filter.setIncludePayload(true);          // Affiche le corps de la requête (body)
        filter.setMaxPayloadLength(10000);       // Limite la taille du body à logger (en caractères)
        filter.setIncludeHeaders(false);         // Si tu veux logger aussi les headers, passe à true (peut générer beaucoup de logs)
        // filter.setAfterMessagePrefix("REQUETE HTTP : "); // prefix optionnel dans le log
        
        return filter;
    }
}
