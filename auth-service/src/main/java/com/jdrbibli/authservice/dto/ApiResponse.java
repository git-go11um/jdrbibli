package com.jdrbibli.authservice.dto;

/**
 * Représente la réponse standard envoyée par le serveur après une opération.
 * <p>
 * Contient un message, un indicateur de succès et éventuellement un token JWT
 * (par exemple après login ou modification de mot de passe).
 * </p>
 */
public class ApiResponse {

    /** Message décrivant le résultat de l'opération */
    private String message;

    /** Indique si l'opération a réussi ou non */
    private boolean success;

    /** Token JWT optionnel renvoyé après certaines opérations */
    private String token;

    /**
     * Constructeur par défaut pour une réponse réussie avec message.
     *
     * @param message message de la réponse
     */
    public ApiResponse(String message) {
        this.message = message;
        this.success = true;
    }

    /**
     * Constructeur complet avec message, succès et token.
     *
     * @param message message de la réponse
     * @param success true si l'opération a réussi, false sinon
     * @param token token JWT optionnel
     */
    public ApiResponse(String message, boolean success, String token) {
        this.message = message;
        this.success = success;
        this.token = token;
    }

    /** @return le message de la réponse */
    public String getMessage() {
        return message;
    }

    /** Définit le message de la réponse */
    public void setMessage(String message) {
        this.message = message;
    }

    /** @return true si l'opération a réussi, false sinon */
    public boolean isSuccess() {
        return success;
    }

    /** Définit l'indicateur de succès */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /** @return le token JWT associé à la réponse, ou null si aucun */
    public String getToken() {
        return token;
    }

    /** Définit le token JWT associé à la réponse */
    public void setToken(String token) {
        this.token = token;
    }
}
