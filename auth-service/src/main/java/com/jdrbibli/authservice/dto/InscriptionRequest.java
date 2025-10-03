package com.jdrbibli.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Représente la requête d'inscription d'un nouvel utilisateur.
 * 
 * Contient les informations nécessaires pour créer un compte utilisateur dans
 * auth-service.
 * Les annotations de validation permettent de s'assurer que les champs
 * obligatoires
 * sont fournis et que l'email est valide.
 * 
 */
public class InscriptionRequest {

    /** Pseudo de l'utilisateur (obligatoire) */
    @NotBlank(message = "Le pseudo est obligatoire")
    private String pseudo;

    /** Adresse email de l'utilisateur (obligatoire et format valide) */
    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    /** Mot de passe de l'utilisateur (obligatoire) */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    /** @return le pseudo de l'utilisateur */
    public String getPseudo() {
        return pseudo;
    }

    /** Définit le pseudo de l'utilisateur */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /** @return l'adresse email de l'utilisateur */
    public String getEmail() {
        return email;
    }

    /** Définit l'adresse email de l'utilisateur */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return le mot de passe de l'utilisateur */
    public String getPassword() {
        return password;
    }

    /** Définit le mot de passe de l'utilisateur */
    public void setPassword(String password) {
        this.password = password;
    }
}
