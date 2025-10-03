package com.jdrbibli.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Point d'entrée principal du microservice {@code auth-service}.
 * <p>
 * Cette classe configure et lance l'application Spring Boot.
 * Elle active également :
 * <ul>
 * <li>la détection automatique des composants Spring via
 * {@link SpringBootApplication}</li>
 * <li>la gestion des dépôts JPA grâce à {@link EnableJpaRepositories}</li>
 * <li>le scan des entités JPA via {@link EntityScan}</li>
 * </ul>
 * <p>
 * Le microservice gère l'authentification et la gestion des utilisateurs
 * (inscription, connexion, réinitialisation de mot de passe, etc.).
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.jdrbibli.authservice.repository")
@EntityScan(basePackages = "com.jdrbibli.authservice.entity")
public class AuthServiceApplication {

    /**
     * Méthode principale qui démarre l'application Spring Boot.
     *
     * @param args arguments passés en ligne de commande.
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
