package com.jdrbibli.authservice.dto;

import java.time.LocalDateTime;

/**
 * Représente la réponse d'erreur renvoyée par l'API.
 * 
 * Contient un message d'erreur, un code HTTP et l'horodatage de l'erreur.
 * Cette classe est utilisée pour centraliser et standardiser les réponses d'erreur.
 * 
 */
public class ErrorResponseDTO {

    /** Message descriptif de l'erreur */
    private String message;

    /** Horodatage de l'erreur */
    private LocalDateTime timestamp;

    /** Code HTTP correspondant à l'erreur */
    private int status;

    /**
     * Constructeur principal.
     *
     * @param message message descriptif de l'erreur
     * @param status code HTTP de l'erreur
     */
    public ErrorResponseDTO(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    /** @return le message d'erreur */
    public String getMessage() {
        return message;
    }

    /** @return l'horodatage de l'erreur */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /** @return le code HTTP de l'erreur */
    public int getStatus() {
        return status;
    }
}
