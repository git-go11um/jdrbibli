package com.jdrbibli.ouvrage_service.entity;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "gammes")
public class Gamme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;

    // Propriétaire de la gamme (ID utilisateur)
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    // Une gamme possède plusieurs ouvrages
    @OneToMany(mappedBy = "gamme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ouvrage> ouvrages;

    // Getters
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public List<Ouvrage> getOuvrages() {
        return ouvrages;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setOuvrages(List<Ouvrage> ouvrages) {
        this.ouvrages = ouvrages;
    }
}
