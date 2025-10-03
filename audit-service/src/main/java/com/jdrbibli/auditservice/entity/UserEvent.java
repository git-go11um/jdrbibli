package com.jdrbibli.auditservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Représente un événement généré par un utilisateur dans le système JdrBibli.
 * 
 * Chaque {@link UserEvent} correspond à une action effectuée par un utilisateur
 * (ex. connexion, changement de mot de passe, modification de profil,
 * suppression de ressource).
 * Ces événements sont stockés dans MongoDB pour permettre le suivi et l'audit
 * des actions utilisateur.
 */
@Document(collection = "user_events")
public class UserEvent {

    /** Identifiant unique de l'événement (généré par MongoDB). */
    @Id
    private String id;

    /** Identifiant de l'utilisateur ayant généré l'événement. */
    private Long userId;

    /** Type d'événement (ex. LOGIN, LOGOUT, PASSWORD_CHANGED). */
    private String eventType;

    /** Date et heure de l'événement, en format {@link Instant}. */
    private Instant timestamp;

    /** Détails optionnels de l'événement. */
    private String details;

    /**
     * Constructeur par défaut requis par Spring Data.
     */
    public UserEvent() {
    }

    /**
     * Constructeur pour créer un événement utilisateur avec toutes les
     * informations.
     *
     * @param userId    identifiant de l'utilisateur.
     * @param eventType type d'événement.
     * @param timestamp date et heure de l'événement.
     * @param details   détails supplémentaires (optionnel).
     */
    public UserEvent(Long userId, String eventType, Instant timestamp, String details) {
        this.userId = userId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
