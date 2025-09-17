package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String pseudo;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "avatar_path")
    private String avatarPath;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Suppression en cascade pour les ludothèques
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLudotheque> ludotheque;

    // Suppression en cascade pour les demandes d'amis envoyées et reçues
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FriendRequest> sentFriendRequests;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FriendRequest> receivedFriendRequests;

    // ----- Getters & Setters -----

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

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<UserLudotheque> getLudotheque() {
        return ludotheque;
    }

    public void setLudotheque(Set<UserLudotheque> ludotheque) {
        this.ludotheque = ludotheque;
    }

    public Set<FriendRequest> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public void setSentFriendRequests(Set<FriendRequest> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
    }

    public Set<FriendRequest> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    public void setReceivedFriendRequests(Set<FriendRequest> receivedFriendRequests) {
        this.receivedFriendRequests = receivedFriendRequests;
    }

    // Ajout d'une relation avec Gamme en cascade
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Gamme> gammes;

    public Set<Gamme> getGammes() {
        return gammes;
    }

    public void setGammes(Set<Gamme> gammes) {
        this.gammes = gammes;
    }
}
