package com.jdrbibli.ouvrage_service.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;

/**
 * Entité représentant un Ouvrage.
 * 
 * Un Ouvrage appartient à une Gamme et possède diverses informations comme le titre, la description,
 * l'éditeur, la date de publication, ainsi que des relations vers d'autres ouvrages ou scénarios.
 * Certains champs comme {@link #scenariosContenus} sont stockés en JSON et exposés via des méthodes utilitaires.
 */
@Entity
@Table(name = "ouvrages")
public class Ouvrage {

    /** Identifiant unique de l'ouvrage */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre de l'ouvrage */
    private String titre;

    /** Description de l'ouvrage */
    private String description;

    /** Gamme à laquelle l'ouvrage appartient */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gamme_id")
    private Gamme gamme;

    /** Version de l'ouvrage */
    private String version;

    /** Type d'ouvrage (livre, supplément, etc.) */
    @Column(name = "type_ouvrage")
    private String typeOuvrage;

    /** Date de publication */
    @Column(name = "date_publication")
    private LocalDate datePublication;

    /** Langue de l'ouvrage */
    private String langue;

    /** Éditeur de l'ouvrage */
    private String editeur;

    /** État physique ou numérique de l'ouvrage */
    private String etat;

    /** ISBN ou identifiant unique de l'ouvrage */
    private String isbn;

    /** Référence à un ouvrage lié */
    @Column(name = "ouvrage_lie")
    private String ouvrageLie;

    /** Référence à un scénario lié */
    @Column(name = "scenario_lie")
    private String scenarioLie;

    /** Indique si l'ouvrage est en prêt */
    private Boolean pret;

    /** Errata éventuels de l'ouvrage */
    private String errata;

    /** Notes personnelles sur l'ouvrage */
    private String notes;

    /** Identifiant du propriétaire de l'ouvrage */
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    /**
     * Contenu des scénarios sous forme JSON stocké en base.
     * 
     * Utiliser {@link #getScenariosContenusList()} et {@link #setScenariosContenusList(List)} pour
     * manipuler la liste de scénarios directement.
     */
    @Column(name = "scenarios_contenus", columnDefinition = "TEXT")
    private String scenariosContenus;

    /** ObjectMapper utilisé pour sérialiser/désérialiser les listes de scénarios */
    @Transient
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Retourne la liste des scénarios contenus en désérialisant le JSON stocké.
     * 
     * @return liste des scénarios ou liste vide si aucun
     */
    public List<String> getScenariosContenusList() {
        if (scenariosContenus == null || scenariosContenus.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(scenariosContenus, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Définit la liste des scénarios et sérialise en JSON pour la base de données.
     * 
     * @param scenarios liste des scénarios
     */
    public void setScenariosContenusList(List<String> scenarios) {
        try {
            this.scenariosContenus = mapper.writeValueAsString(scenarios);
        } catch (Exception e) {
            e.printStackTrace();
            this.scenariosContenus = "[]";
        }
    }

    /** Liste des autres ouvrages appartenant à la même gamme */
    @ElementCollection
    @CollectionTable(name = "ouvrage_autres_ouvrages_gamme", joinColumns = @JoinColumn(name = "ouvrage_id"))
    @Column(name = "ouvrage")
    private List<String> autresOuvragesGamme = new ArrayList<>();

    /** URL de l'image associée à l'ouvrage */
    @Column(name = "image_url")
    private String imageUrl;

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
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
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public List<String> getAutresOuvragesGamme() { return autresOuvragesGamme; }
    public void setAutresOuvragesGamme(List<String> autresOuvragesGamme) { this.autresOuvragesGamme = autresOuvragesGamme; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getScenariosContenus() { return scenariosContenus; }
    public void setScenariosContenus(String scenariosContenus) { this.scenariosContenus = scenariosContenus; }
}
