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

/**
 * Configuration de sécurité pour le microservice User-Service.
 * 
 * Cette classe configure Spring Security pour :
 * <ul>
 * <li>Désactiver CSRF</li>
 * <li>Utiliser une politique de session stateless</li>
 * <li>Autoriser certaines routes publiques (test, création utilisateur,
 * recherche, mise à jour)</li>
 * <li>Appliquer un filtre JWT avant le filtre d'authentification standard</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructeur injectant le filtre JWT.
     *
     * @param jwtAuthenticationFilter le filtre de validation des JWT
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configuration de la chaîne de filtres de sécurité.
     *
     * @param http l'objet HttpSecurity pour configurer la sécurité HTTP
     * @return la SecurityFilterChain configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/test/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/search").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").permitAll()
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean d'AuthenticationManager pour l'authentification des utilisateurs.
     *
     * @param authConfig la configuration d'authentification
     * @return un AuthenticationManager prêt à l'emploi
     * @throws Exception en cas d'erreur
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
