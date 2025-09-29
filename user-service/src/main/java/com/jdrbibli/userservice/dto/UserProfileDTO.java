package com.jdrbibli.userservice.dto;

public class UserProfileDTO {
    private Long id;
    private String pseudo;
    private String email;
    private String avatarUrl;

    // Constructeurs
    public UserProfileDTO() {
    }

    public UserProfileDTO(Long id, String pseudo, String email) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
    }

    // Constructeur sans id (pour retour ou création côté front si tu veux)
    public UserProfileDTO(String pseudo, String email, String avatarUrl) {
        this.pseudo = pseudo;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public UserProfileDTO(Long id, String pseudo, String email, String avatarUrl) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    // Getters / Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
