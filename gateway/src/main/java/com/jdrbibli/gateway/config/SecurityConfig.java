package com.jdrbibli.gateway.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de sécurité du gateway-service.
 * 
 * Cette classe définit les règles de sécurité pour toutes les requêtes HTTP
 * entrantes dans le gateway. 
 * 
 * Elle utilise Spring WebFlux Security et configure notamment :
 * <ul>
 *   <li>Désactivation du CSRF (non nécessaire pour une API REST sécurisée par JWT)</li>
 *   <li>Configuration CORS pour autoriser le frontend Angular (http://localhost:4200)</li>
 *   <li>Autorisation des routes publiques comme /login, /register et /password-reset/**</li>
 *   <li>Désactivation de l'authentification HTTP Basic et du formulaire de login</li>
 * </ul>
 * 
 * ⚠️ Actuellement, le gateway ne valide plus aucun JWT et laisse passer toutes les requêtes.
 */
@Configuration
public class SecurityConfig {

    /**
     * Crée la chaîne de filtres de sécurité pour Spring WebFlux.
     *
     * @param http configuration HTTP fournie par Spring Security.
     * @return un {@link SecurityWebFilterChain} configuré pour le gateway-service.
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:4200"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/api/auth/login").permitAll()
                        .pathMatchers("/api/auth/register").permitAll()
                        .pathMatchers("/api/auth/password-reset/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().permitAll() // ✅ le gateway ne valide plus rien
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        return http.build();
    }
}
