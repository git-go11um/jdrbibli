package com.jdrbibli.authservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité représentant un token de réinitialisation de mot de passe.
 * <p>
 * Chaque token est associé à un utilisateur et possède une date d'expiration.
 * Utilisé pour les processus de "password reset" dans AuthService.
 * </p>
 */
@Entity
public class PasswordResetToken {

    /** Identifiant unique du token */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Valeur du token (chaîne unique) */
    private String token;

    /** Date et heure d'expiration du token */
    private LocalDateTime expiryDate;

    /** Utilisateur associé au token */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** Constructeur par défaut requis par JPA */
    public PasswordResetToken() {
    }

    /**
     * Constructeur complet.
     *
     * @param token la valeur du token
     * @param expiryDate date d'expiration
     * @param user utilisateur associé
     */
    public PasswordResetToken(String token, LocalDateTime expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    /** @return l'identifiant du token */
    public Long getId() {
        return id;
    }

    /** Définit l'identifiant du token */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return la valeur du token */
    public String getToken() {
        return token;
    }

    /** Définit la valeur du token */
    public void setToken(String token) {
        this.token = token;
    }

    /** @return la date d'expiration */
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    /** Définit la date d'expiration */
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    /** @return l'utilisateur associé au token */
    public User getUser() {
        return user;
    }

    /** Définit l'utilisateur associé au token */
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                ", user=" + (user != null ? user.getPseudo() : "null") +
                '}';
    }
}
