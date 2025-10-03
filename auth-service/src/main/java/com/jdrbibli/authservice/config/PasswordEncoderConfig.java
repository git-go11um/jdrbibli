package com.jdrbibli.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration pour le chiffrement des mots de passe.
 * <p>
 * Cette classe fournit un {@link PasswordEncoder} basé sur BCrypt pour
 * sécuriser
 * les mots de passe des utilisateurs. Le {@link PasswordEncoder} est utilisé
 * dans tout le microservice AuthService pour encoder les mots de passe à
 * l'inscription
 * et vérifier les mots de passe lors de la connexion.
 * </p>
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Crée un {@link PasswordEncoder} utilisant l'algorithme BCrypt.
     *
     * @return un {@link PasswordEncoder} prêt à l'utilisation
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
