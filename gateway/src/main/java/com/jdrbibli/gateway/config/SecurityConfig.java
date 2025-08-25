package com.jdrbibli.gateway.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:4200"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeExchange(exchanges -> exchanges
                        // Autoriser toutes les requêtes OPTIONS (préflight)
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Endpoints publics du microservice auth-service
                        .pathMatchers("/api/auth/login").permitAll()
                        .pathMatchers("/api/auth/register").permitAll()
                        .pathMatchers("/api/auth/password-reset/request").permitAll()
                        .pathMatchers("/api/auth/password-reset/verify-code").permitAll()
                        .pathMatchers("/api/auth/password-reset/confirm").permitAll()
                        .pathMatchers("/api/auth/validate-reset-code").permitAll()
                        .pathMatchers("/api/auth/password-reset/change").permitAll()
                        

                        // Toutes les autres requêtes nécessitent authentification
                        .anyExchange().authenticated())
                .httpBasic().disable()
                .formLogin().disable();

        return http.build();
    }
}
