package com.jdrbibli.auditservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Représente un log d'audit stocké dans MongoDB.
 * <p>
 * Chaque log correspond à une action effectuée par un service
 * dans l'écosystème JdrBibli (ex. mise à jour d'utilisateur, suppression de
 * ressource).
 * Les informations enregistrées permettent de tracer et analyser les
 * événements.
 */
@Document(collection = "audit_logs")
public class AuditLog {

    /** Identifiant unique du log (généré par MongoDB). */
    @Id
    private String id;

    /**
     * Nom du microservice à l'origine de l'action (ex. auth-service, user-service).
     */
    private String serviceName;

    /** Type d'action effectuée (ex. USER_UPDATED, PASSWORD_CHANGED). */
    private String action;

    /** Détails complémentaires décrivant l'action réalisée. */
    private String details;

    /** Date et heure de l'enregistrement du log (générée automatiquement). */
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Constructeur par défaut requis par Spring Data.
     */
    public AuditLog() {
    }

    /**
     * Constructeur pour créer un log d'audit avec les informations principales.
     *
     * @param serviceName nom du service ayant généré le log.
     * @param action      type d'action réalisée.
     * @param details     détails complémentaires de l'action.
     */
    public AuditLog(String serviceName, String action, String details) {
        this.serviceName = serviceName;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
