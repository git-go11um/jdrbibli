package com.jdrbibli.userservice.entity;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entité représentant le profil d'un utilisateur.
 * Contient les informations personnelles, l'avatar, la ludothèque et les relations avec amis et gammes.
 */
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    /** Identifiant unique de l'utilisateur. */
    @Id
    private Long id;

    /** Pseudo de l'utilisateur. */
    private String pseudo;

    /** Email de l'utilisateur. */
    private String email;

    /** URL publique de l'avatar de l'utilisateur (accessible via API). */
    @Column(nullable = true)
    private String avatarUrl;

    /** Chemin local du fichier avatar sur le serveur. */
    @Column(nullable = true)
    private String avatarPath;

    /** Liste des identifiants des ouvrages possédés par l'utilisateur. */
    @ElementCollection
    @CollectionTable(name = "user_ludotheque", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "ouvrage_id")
    private List<Long> ouvrageIds;

    /** Ludothèque complète avec détails des ouvrages (non persistée). */
    @Transient
    private List<OuvrageDTO> ludotheque;

    /** Liste des amis de l'utilisateur (relation Many-to-Many). */
    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<UserProfile> friends;

    /** Ensemble des gammes créées par l'utilisateur. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Gamme> gammes = new HashSet<>();

    /** Retourne l'identifiant de l'utilisateur. */
    public Long getId() {
        return id;
    }

    /** Définit l'identifiant de l'utilisateur. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Retourne le pseudo de l'utilisateur. */
    public String getPseudo() {
        return pseudo;
    }

    /** Définit le pseudo de l'utilisateur. */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /** Retourne l'email de l'utilisateur. */
    public String getEmail() {
        return email;
    }

    /** Définit l'email de l'utilisateur. */
    public void setEmail(String email) {
        this.email = email;
    }

    /** Retourne l'URL de l'avatar public. */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /** Définit l'URL de l'avatar public. */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /** Retourne la liste des IDs des ouvrages dans la ludothèque. */
    public List<Long> getOuvrageIds() {
        return ouvrageIds;
    }

    /** Définit la liste des IDs des ouvrages dans la ludothèque. */
    public void setOuvrageIds(List<Long> ouvrageIds) {
        this.ouvrageIds = ouvrageIds;
    }

    /** Retourne la ludothèque complète avec détails des ouvrages. */
    public List<OuvrageDTO> getLudotheque() {
        return ludotheque;
    }

    /** Définit la ludothèque complète avec détails des ouvrages. */
    public void setLudotheque(List<OuvrageDTO> ludotheque) {
        this.ludotheque = ludotheque;
    }

    /** Retourne la liste des amis de l'utilisateur. */
    public List<UserProfile> getFriends() {
        return friends;
    }

    /** Définit la liste des amis de l'utilisateur. */
    public void setFriends(List<UserProfile> friends) {
        this.friends = friends;
    }

    /** Retourne le chemin local de l'avatar sur le serveur. */
    public String getAvatarPath() {
        return avatarPath;
    }

    /** Définit le chemin local de l'avatar sur le serveur. */
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    /** Retourne l'ensemble des gammes créées par l'utilisateur. */
    public Set<Gamme> getGammes() {
        return gammes;
    }

    /** Définit l'ensemble des gammes créées par l'utilisateur. */
    public void setGammes(Set<Gamme> gammes) {
        this.gammes = gammes;
    }
}
