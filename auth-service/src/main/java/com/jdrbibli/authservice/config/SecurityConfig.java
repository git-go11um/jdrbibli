package com.jdrbibli.authservice.config;

import com.jdrbibli.authservice.security.CustomUserDetailsService;
import com.jdrbibli.authservice.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de la sécurité pour le microservice AuthService.
 * 
 * Cette classe configure Spring Security pour le service d'authentification.
 * Elle définit les endpoints publics et sécurisés, le filtre JWT, ainsi que
 * les beans nécessaires pour l'authentification et l'encodage des mots de
 * passe.
 * 
 * 
 * Les endpoints suivants sont publics :
 * <ul>
 * <li>POST /auth/login</li>
 * <li>POST /auth/register</li>
 * <li>POST /auth/password-reset/**</li>
 * <li>/auth/refresh</li>
 * </ul>
 * Tous les autres endpoints sous /auth/** nécessitent une authentification.
 * 
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur de SecurityConfig.
     *
     * @param userDetailsService      service pour charger les utilisateurs
     * @param jwtAuthenticationFilter filtre pour valider les JWT
     * @param passwordEncoder         encodeur de mot de passe
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Bean pour le {@link AuthenticationProvider} utilisant DAO.
     * 
     * Utilise {@link DaoAuthenticationProvider} avec
     * {@link CustomUserDetailsService}
     * et {@link PasswordEncoder} pour authentifier les utilisateurs.
     * 
     *
     * @return un {@link AuthenticationProvider} configuré
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Bean pour le {@link AuthenticationManager}.
     *
     * @param config configuration d'authentification
     * @return un {@link AuthenticationManager} prêt à l'utilisation
     * @throws Exception si l'initialisation échoue
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean pour la chaîne de filtres de sécurité {@link SecurityFilterChain}.
     * 
     * Configure :
     * <ul>
     * <li>Désactivation de CSRF</li>
     * <li>Politique de session stateless</li>
     * <li>Filtre JWT avant UsernamePasswordAuthenticationFilter</li>
     * <li>Autorisation des endpoints publics et sécurisation des autres</li>
     * </ul>
     * 
     *
     * @param http configuration HTTP
     * @return un {@link SecurityFilterChain} configuré
     * @throws Exception si la configuration échoue
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Endpoints publics
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/auth/login", "/auth/register", "/auth/password-reset/**", "/auth/refresh")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/password-reset/**").permitAll()
                        .requestMatchers("/auth/refresh").permitAll()
                        // Endpoints nécessitant authentification
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers("/auth/profile/**").authenticated()
                        .requestMatchers("/auth/**").authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
