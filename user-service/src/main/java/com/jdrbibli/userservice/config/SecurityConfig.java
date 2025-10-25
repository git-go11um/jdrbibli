package com.jdrbibli.userservice.config;

import com.jdrbibli.userservice.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // --- Routes publiques ---
                .requestMatchers(
                    "/api/auth/**",
                    "/auth/**",
                    "/actuator/**"
                ).permitAll()

                // --- Routes utilisateurs accessibles par Auth-Service ---
                .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()   // ✅ permet les updates du auth-service
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()

                // --- Routes friends publiques (si besoin) ---
                .requestMatchers("/friends/**", "/api/friends/**").permitAll()

                // --- Tout le reste doit être authentifié ---
                .anyRequest().authenticated()
            )
            // ✅ On ajoute le filtre JWT
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
