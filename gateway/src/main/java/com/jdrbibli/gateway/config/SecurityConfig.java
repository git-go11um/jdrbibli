package com.jdrbibli.gateway.config;

import com.jdrbibli.gateway.security.JwtAuthenticationManager;
import com.jdrbibli.gateway.security.JwtSecurityContextRepository;
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

    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtSecurityContextRepository jwtSecurityContextRepository;

    public SecurityConfig(JwtAuthenticationManager jwtAuthenticationManager,
            JwtSecurityContextRepository jwtSecurityContextRepository) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.jwtSecurityContextRepository = jwtSecurityContextRepository;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var c = new org.springframework.web.cors.CorsConfiguration();
                    c.setAllowedOrigins(List.of("http://localhost:4200"));
                    c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    c.setAllowedHeaders(List.of("*"));
                    c.setExposedHeaders(List.of("Authorization"));
                    c.setAllowCredentials(true);
                    return c;
                }))
                .securityContextRepository(jwtSecurityContextRepository)
                .authenticationManager(jwtAuthenticationManager)
                .authorizeExchange(exchanges -> exchanges
                        // Préflight CORS
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth-service (login, register, reset)
                        .pathMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/password-reset/**")
                        .permitAll()

                        // ✅ On autorise l'accès public aux images et à l'actuator
                        .pathMatchers("/uploads/**", "/actuator/**").permitAll()

                        // Tout le reste = sécurisé
                        .anyExchange().authenticated())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}
