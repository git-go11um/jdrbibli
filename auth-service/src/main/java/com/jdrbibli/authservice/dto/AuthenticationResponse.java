package com.jdrbibli.authservice.dto;

/**
 * Représente la réponse renvoyée lors d'une authentification réussie.
 * <p>
 * Contient le token JWT généré et les informations de l'utilisateur associé.
 * </p>
 */
public class AuthenticationResponse {

    /** Token JWT renvoyé après login ou rafraîchissement */
    private String token;

    /** Informations de l'utilisateur authentifié */
    private UserResponseDTO user;

    /** Constructeur par défaut requis pour la sérialisation/désérialisation */
    public AuthenticationResponse() {
    }

    /**
     * Constructeur complet.
     *
     * @param token token JWT généré pour l'utilisateur
     * @param user informations de l'utilisateur authentifié
     */
    public AuthenticationResponse(String token, UserResponseDTO user) {
        this.token = token;
        this.user = user;
    }

    /** @return le token JWT */
    public String getToken() {
        return token;
    }

    /** Définit le token JWT */
    public void setToken(String token) {
        this.token = token;
    }

    /** @return les informations de l'utilisateur */
    public UserResponseDTO getUser() {
        return user;
    }

    /** Définit les informations de l'utilisateur */
    public void setUser(UserResponseDTO user) {
        this.user = user;
    }
}
