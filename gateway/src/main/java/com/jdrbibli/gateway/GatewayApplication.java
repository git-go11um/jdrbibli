package com.jdrbibli.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal du microservice {@code gateway-service}.
 * 
 * Cette classe configure et lance l'application Spring Boot.
 * Le gateway-service agit comme point d'entrée unique pour tous les autres microservices
 * du projet JdrBibli, en gérant :
 * <ul>
 *   <li>le routage des requêtes vers les microservices appropriés</li>
 *   <li>la sécurité via JWT (si activée)</li>
 *   <li>la configuration CORS et les filtres globaux</li>
 * </ul>
 * 
 * Elle active la configuration automatique Spring Boot et démarre le contexte de l'application.
 */
@SpringBootApplication
public class GatewayApplication {

    /**
     * Méthode principale qui démarre l'application Spring Boot.
     *
     * @param args arguments passés en ligne de commande.
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
