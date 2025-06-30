package com.jdrbibli.ouvrage_service.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ouvrages")
public class Ouvrage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation vers Gamme (facultative si tu veux juste stocker le nom)
    @ManyToOne
    @JoinColumn(name = "gamme_id")
    private Gamme gamme;

    private String version;
    private String typeOuvrage;
    private LocalDate datePublication;
    private String langue;
    private String editeur;
    private String etat;
    private String isbn;
    private String ouvrageLie;
    private String scenarioLie;
    private Boolean pret;
    private String errata;
    private String notes;

    @ElementCollection
    private List<String> scenariosContenus;

    @ElementCollection
    private List<String> autresOuvragesGamme;

    // Getters & setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Gamme getGamme() { return gamme; }
    public void setGamme(Gamme gamme) { this.gamme = gamme; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getTypeOuvrage() { return typeOuvrage; }
    public void setTypeOuvrage(String typeOuvrage) { this.typeOuvrage = typeOuvrage; }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) { this.datePublication = datePublication; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public String getEditeur() { return editeur; }
    public void setEditeur(String editeur) { this.editeur = editeur; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getOuvrageLie() { return ouvrageLie; }
    public void setOuvrageLie(String ouvrageLie) { this.ouvrageLie = ouvrageLie; }

    public String getScenarioLie() { return scenarioLie; }
    public void setScenarioLie(String scenarioLie) { this.scenarioLie = scenarioLie; }

    public Boolean getPret() { return pret; }
    public void setPret(Boolean pret) { this.pret = pret; }

    public String getErrata() { return errata; }
    public void setErrata(String errata) { this.errata = errata; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<String> getScenariosContenus() { return scenariosContenus; }
    public void setScenariosContenus(List<String> scenariosContenus) { this.scenariosContenus = scenariosContenus; }

    public List<String> getAutresOuvragesGamme() { return autresOuvragesGamme; }
    public void setAutresOuvragesGamme(List<String> autresOuvragesGamme) { this.autresOuvragesGamme = autresOuvragesGamme; }
}
