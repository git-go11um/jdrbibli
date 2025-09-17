package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;
import java.util.List;

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
    private User user;

    // Liste des ouvrages associés à cette gamme
    @OneToMany(mappedBy = "gamme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ouvrage> ouvrages;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Ouvrage> getOuvrages() {
        return ouvrages;
    }

    public void setOuvrages(List<Ouvrage> ouvrages) {
        this.ouvrages = ouvrages;
    }

    // Getters et Setters

}
