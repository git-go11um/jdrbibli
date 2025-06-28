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

    @OneToMany(mappedBy = "gamme")
    private List<Ouvrage> ouvrages;

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Ouvrage> getOuvrages() { return ouvrages; }
    public void setOuvrages(List<Ouvrage> ouvrages) { this.ouvrages = ouvrages; }
}
