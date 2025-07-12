package com.jdrbibli.authservice.dto;

public class ApiResponse {
    private String message;
    private boolean success;
    private String token; // Nouveau champ pour le token JWT

    // Constructeur avec message et succès (comme dans la version précédente)
    public ApiResponse(String message) {
        this.message = message;
        this.success = true; // Par défaut, succès
    }

    // Constructeur avec message, succès et token (pour l'authentification)
    public ApiResponse(String message, boolean success, String token) {
        this.message = message;
        this.success = success;
        this.token = token;
    }

    // Getters et Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
