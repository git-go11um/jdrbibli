package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "gammes")
public class Gamme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    // Référence à l'utilisateur qui possède la gamme (relation ManyToOne)
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserProfile user;

    // Liste des ouvrages associés à cette gamme
    @OneToMany(mappedBy = "gamme", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ouvrage> ouvrages = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public Set<Ouvrage> getOuvrages() {
        return ouvrages;
    }

    public void setOuvrages(Set<Ouvrage> ouvrages) {
        this.ouvrages = ouvrages;
    }

}
