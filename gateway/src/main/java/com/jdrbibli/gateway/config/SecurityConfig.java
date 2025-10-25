package com.jdrbibli.gateway.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // Désactivation CSRF (API REST)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Configuration CORS autorisant Angular
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:4200"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // Définition des autorisations
                .authorizeExchange(exchanges -> exchanges
                        // Autorise les requêtes CORS preflight
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth-service (inscription, login, reset)
                        .pathMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/password-reset/**")
                        .permitAll()

                        // Actuator
                        .pathMatchers("/actuator/**").permitAll()

                        // Friends publics (liste accessible sans token)
                        .pathMatchers(HttpMethod.GET, "/api/friends/**").permitAll()

                        // Users (si tu veux garder /me protégé)
                        .pathMatchers("/api/users/**").authenticated()

                        // Tout le reste nécessite authentification (ex: /api/users/**, /me, etc.)
                        .anyExchange().authenticated())
                // Pas de form login ni HTTP Basic
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        return http.build();
    }
}
