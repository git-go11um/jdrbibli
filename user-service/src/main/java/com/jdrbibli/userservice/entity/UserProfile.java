package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pseudo;
    private String email;
    private String avatarUrl;

    // Pour plus tard : relations avec amis et ludothèque
    // @OneToMany(mappedBy = "sender") private List<FriendRequest> sentRequests;
    // @OneToMany(mappedBy = "receiver") private List<FriendRequest>
    // receivedRequests;
    // @OneToMany private List<GameItem> ludotheque;

    @OneToMany
    @JoinTable(name = "user_ludotheque", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "ouvrage_id"))
    private List<Ouvrage> ludotheque;

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
}
