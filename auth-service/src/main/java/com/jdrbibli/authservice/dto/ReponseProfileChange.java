package com.jdrbibli.authservice.dto;

/**
 * Représente la réponse envoyée après une modification du profil utilisateur.
 * 
 * Contient un message informatif sur le résultat de l'opération et,
 * si applicable, un nouveau token JWT mis à jour après le changement de pseudo ou d'autres informations sensibles.
 * 
 */
public class ReponseProfileChange {

    /** Message décrivant le résultat de la modification du profil */
    private String message;

    /** Nouveau token JWT généré après la modification du profil */
    private String newToken;

    /** Constructeur par défaut */
    public ReponseProfileChange() {
    }

    /**
     * Constructeur avec message et nouveau token.
     *
     * @param message  message de retour
     * @param newToken nouveau token JWT
     */
    public ReponseProfileChange(String message, String newToken) {
        this.message = message;
        this.newToken = newToken;
    }

    /** @return le message décrivant le résultat de l'opération */
    public String getMessage() {
        return message;
    }

    /** Définit le message décrivant le résultat de l'opération */
    public void setMessage(String message) {
        this.message = message;
    }

    /** @return le nouveau token JWT */
    public String getNewToken() {
        return newToken;
    }

    /** Définit le nouveau token JWT */
    public void setNewToken(String newToken) {
        this.newToken = newToken;
    }

}
