package com.jdrbibli.ouvrage_service.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ouvrages")
public class Ouvrage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gamme_id")
    private Gamme gamme;

    private String version;

    @Column(name = "type_ouvrage")
    private String typeOuvrage;

    @Column(name = "date_publication")
    private LocalDate datePublication;

    private String langue;
    private String editeur;
    private String etat;
    private String isbn;

    @Column(name = "ouvrage_lie")
    private String ouvrageLie;

    @Column(name = "scenario_lie")
    private String scenarioLie;

    private Boolean pret;
    private String errata;
    private String notes;

    @ElementCollection
    @CollectionTable(name = "ouvrage_scenarios_contenus", joinColumns = @JoinColumn(name = "ouvrage_id"))
    @Column(name = "scenario")
    private List<String> scenariosContenus = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ouvrage_autres_ouvrages_gamme", joinColumns = @JoinColumn(name = "ouvrage_id"))
    @Column(name = "ouvrage")
    private List<String> autresOuvragesGamme = new ArrayList<>();

    // Nouveau champ liensMedias (liens vers médias externes)
    @ElementCollection
    @CollectionTable(name = "ouvrage_liens_medias", joinColumns = @JoinColumn(name = "ouvrage_id"))
    @Column(name = "lien")
    private List<String> liensMedias = new ArrayList<>();

    // Getters & setters (ajoute liensMedias)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Gamme getGamme() {
        return gamme;
    }

    public void setGamme(Gamme gamme) {
        this.gamme = gamme;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTypeOuvrage() {
        return typeOuvrage;
    }

    public void setTypeOuvrage(String typeOuvrage) {
        this.typeOuvrage = typeOuvrage;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public String getEditeur() {
        return editeur;
    }

    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getOuvrageLie() {
        return ouvrageLie;
    }

    public void setOuvrageLie(String ouvrageLie) {
        this.ouvrageLie = ouvrageLie;
    }

    public String getScenarioLie() {
        return scenarioLie;
    }

    public void setScenarioLie(String scenarioLie) {
        this.scenarioLie = scenarioLie;
    }

    public Boolean getPret() {
        return pret;
    }

    public void setPret(Boolean pret) {
        this.pret = pret;
    }

    public String getErrata() {
        return errata;
    }

    public void setErrata(String errata) {
        this.errata = errata;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getScenariosContenus() {
        return scenariosContenus;
    }

    public void setScenariosContenus(List<String> scenariosContenus) {
        this.scenariosContenus = scenariosContenus;
    }

    public List<String> getAutresOuvragesGamme() {
        return autresOuvragesGamme;
    }

    public void setAutresOuvragesGamme(List<String> autresOuvragesGamme) {
        this.autresOuvragesGamme = autresOuvragesGamme;
    }

    public List<String> getLiensMedias() {
        return liensMedias;
    }

    public void setLiensMedias(List<String> liensMedias) {
        this.liensMedias = liensMedias;
    }
}
