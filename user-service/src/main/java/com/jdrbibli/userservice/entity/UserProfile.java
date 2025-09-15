package com.jdrbibli.userservice.entity;

import com.jdrbibli.userservice.dto.OuvrageDTO;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    /* @GeneratedValue(strategy = GenerationType.IDENTITY) */
    private Long id;

    private String pseudo;
    private String email;

    @Column(nullable = true)
    private String avatarUrl;

    @Column(nullable = true)
    private String avatarPath;

    /**
     * Liste d'IDs des ouvrages liés à cet utilisateur (persistée en base).
     */
    @ElementCollection
    @CollectionTable(name = "user_ludotheque", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "ouvrage_id")
    private List<Long> ouvrageIds;

    /**
     * Liste des ouvrages complets récupérés depuis ouvrage-service (non persistée).
     */
    @Transient
    private List<OuvrageDTO> ludotheque;

    /**
     * Liste des amis (relation bidirectionnelle ManyToMany persistée en base).
     */
    @ManyToMany
    @JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<UserProfile> friends;

    // Getters et setters

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

    public List<Long> getOuvrageIds() {
        return ouvrageIds;
    }

    public void setOuvrageIds(List<Long> ouvrageIds) {
        this.ouvrageIds = ouvrageIds;
    }

    public List<OuvrageDTO> getLudotheque() {
        return ludotheque;
    }

    public void setLudotheque(List<OuvrageDTO> ludotheque) {
        this.ludotheque = ludotheque;
    }

    public List<UserProfile> getFriends() {
        return friends;
    }

    public void setFriends(List<UserProfile> friends) {
        this.friends = friends;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
