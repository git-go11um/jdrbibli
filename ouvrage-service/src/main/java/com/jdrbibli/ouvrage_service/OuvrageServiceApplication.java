package com.jdrbibli.ouvrage_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal du microservice Ouvrage-Service.
 * 
 * Cette classe démarre l'application Spring Boot et initialise le contexte.
 */
@SpringBootApplication
public class OuvrageServiceApplication {

    /**
     * Méthode principale qui lance le microservice.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(OuvrageServiceApplication.class, args);
    }

}
