package com.jdrbibli.ouvrage_service.config;

import com.jdrbibli.ouvrage_service.security.JwtAuthenticationFilter;
import com.jdrbibli.ouvrage_service.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Configuration de la sécurité pour le microservice {@code ouvrage-service}.
 * 
 * Cette classe configure Spring Security pour gérer :
 * <ul>
 * <li>L'authentification via JWT avec {@link JwtAuthenticationFilter} et
 * {@link JwtTokenProvider}</li>
 * <li>La gestion des utilisateurs avec {@link UserDetailsService}</li>
 * <li>Le chiffrement des mots de passe avec {@link BCryptPasswordEncoder}</li>
 * <li>La configuration CORS pour autoriser le front Angular sur
 * localhost:4200</li>
 * <li>La désactivation de CSRF pour les requêtes API REST</li>
 * </ul>
 * 
 * Les endpoints liés aux ouvrages et gammes sont actuellement ouverts à tous
 * les accès.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Constructeur de la configuration de sécurité.
     *
     * @param jwtTokenProvider   fournisseur de tokens JWT pour l'authentification.
     * @param userDetailsService service Spring Security pour charger les
     *                           informations des utilisateurs.
     */
    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    // Constructeur supplémentaire pour tests unitaires
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = null; // ou Mockito.mock(UserDetailsService.class) si tu veux
    }

    /**
     * Définit la chaîne de filtres de sécurité HTTP pour l'application.
     *
     * @param http                  configuration HttpSecurity.
     * @param authenticationManager gestionnaire d'authentification.
     * @return la chaîne de filtres de sécurité.
     * @throws Exception si la configuration échoue.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        http
                .cors().and()
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/ouvrage/ouvrages/**").permitAll()
                        .requestMatchers("/api/ouvrage/gammes/**").permitAll()
                        .anyRequest().permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, authenticationManager),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configure le {@link AuthenticationManager} avec le {@link UserDetailsService}
     * et le {@link PasswordEncoder} pour Spring Security.
     *
     * @param http configuration HttpSecurity.
     * @return le gestionnaire d'authentification.
     * @throws Exception si la configuration échoue.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    /**
     * Fournit un {@link PasswordEncoder} basé sur BCrypt pour sécuriser les mots de
     * passe.
     *
     * @return le {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure un filtre CORS pour autoriser les requêtes du front Angular
     * sur localhost:4200 avec tous les headers et méthodes.
     *
     * @return le {@link CorsFilter}.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
