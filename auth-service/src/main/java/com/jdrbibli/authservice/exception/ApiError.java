package com.jdrbibli.authservice.exception;

import java.time.LocalDateTime;

/**
 * Classe représentant une erreur API standardisée.
 * 
 * Cette classe est utilisée pour renvoyer des réponses d'erreur JSON cohérentes
 * aux clients lorsqu'une exception est levée dans le backend.
 * 
 */
public class ApiError {

    /** Code HTTP de la réponse d'erreur (ex: 404, 500) */
    private int status;

    /** Message détaillé décrivant l'erreur */
    private String message;

    /** Horodatage indiquant le moment de l'erreur */
    private LocalDateTime timestamp;

    /**
     * Constructeur complet pour ApiError
     *
     * @param status    code HTTP de l'erreur
     * @param message   message explicatif
     * @param timestamp date et heure de l'erreur
     */
    public ApiError(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    // ----------------------- Getters & Setters -----------------------

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
