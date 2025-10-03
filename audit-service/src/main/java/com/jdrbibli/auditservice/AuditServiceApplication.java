package com.jdrbibli.auditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal du microservice {@code audit-service}.
 * <p>
 * Cette classe configure et lance l'application Spring Boot.
 * Le microservice gère la collecte et la persistance :
 * <ul>
 *   <li>des logs d'audit liés aux actions des différents microservices ({@link com.jdrbibli.auditservice.entity.AuditLog})</li>
 *   <li>des événements utilisateur ({@link com.jdrbibli.auditservice.entity.UserEvent})</li>
 * </ul>
 * <p>
 * Elle active la configuration automatique Spring Boot et démarre le contexte de l'application.
 */
@SpringBootApplication
public class AuditServiceApplication {

    /**
     * Méthode principale qui démarre l'application Spring Boot.
     *
     * @param args arguments passés en ligne de commande.
     */
    public static void main(String[] args) {
        SpringApplication.run(AuditServiceApplication.class, args);
    }
}
