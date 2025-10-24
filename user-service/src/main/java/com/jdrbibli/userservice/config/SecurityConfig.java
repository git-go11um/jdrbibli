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
                        // Routes publiques
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/test/**").permitAll()

                        // 🔥 Autorise tous les POST venant d'auth-service (création utilisateur)
                        .requestMatchers(HttpMethod.POST, "/api/users/**", "/users/**").permitAll()

                        // 🔥 Autorise les recherches et affichages utilisateurs
                        .requestMatchers(HttpMethod.GET, "/api/users/**", "/users/**").permitAll()

                        // Tout le reste nécessite un token JWT
                        .anyRequest().authenticated()
                )
                // Important : le filtre JWT doit passer avant le filtre UsernamePassword
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
