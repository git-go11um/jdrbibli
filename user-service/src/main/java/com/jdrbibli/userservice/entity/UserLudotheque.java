package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;

/**
 * Entité représentant l'association entre un utilisateur et un ouvrage dans sa ludothèque.
 * Chaque enregistrement correspond à un ouvrage possédé par un utilisateur.
 */
@Entity
@Table(name = "user_ludotheque")
public class UserLudotheque {

    /** Identifiant unique de l'association utilisateur-ouvrage. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** L'utilisateur propriétaire de l'ouvrage. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

    /** L'ouvrage ajouté à la ludothèque de l'utilisateur. */
    @ManyToOne
    @JoinColumn(name = "ouvrage_id", nullable = false)
    private Ouvrage ouvrage;

    /** Retourne l'identifiant de l'association. */
    public Long getId() {
        return id;
    }

    /** Définit l'identifiant de l'association. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Retourne l'utilisateur propriétaire de l'ouvrage. */
    public UserProfile getUser() {
        return user;
    }

    /** Définit l'utilisateur propriétaire de l'ouvrage. */
    public void setUser(UserProfile user) {
        this.user = user;
    }

    /** Retourne l'ouvrage associé à l'utilisateur. */
    public Ouvrage getOuvrage() {
        return ouvrage;
    }

    /** Définit l'ouvrage associé à l'utilisateur. */
    public void setOuvrage(Ouvrage ouvrage) {
        this.ouvrage = ouvrage;
    }
}
