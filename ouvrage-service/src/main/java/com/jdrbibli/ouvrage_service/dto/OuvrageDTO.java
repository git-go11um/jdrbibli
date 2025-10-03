package com.jdrbibli.ouvrage_service.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object représentant un {@link com.jdrbibli.ouvrage_service.entity.Ouvrage}.
 * <p>
 * Utilisé pour transférer les informations d'un ouvrage entre le backend et le frontend.
 * Contient toutes les informations descriptives, de publication, de relation à la gamme et à d'autres ouvrages,
 * ainsi que l'état de prêt et les liens vers les images.
 */
public class OuvrageDTO {

    /** Identifiant unique de l'ouvrage */
    private Long id;

    /** Titre de l'ouvrage */
    private String titre;

    /** Description de l'ouvrage */
    private String description;

    /** Identifiant de la gamme à laquelle l'ouvrage appartient */
    private Long gammeId;

    /** Version de l'ouvrage */
    private String version;

    /** Type de l'ouvrage (ex: Livre, Module, Scénario) */
    private String typeOuvrage;

    /** Date de publication */
    private LocalDate datePublication;

    /** Langue de l'ouvrage */
    private String langue;

    /** Éditeur */
    private String editeur;

    /** État physique de l'ouvrage */
    private String etat;

    /** ISBN si disponible */
    private String isbn;

    /** Ouvrage lié */
    private String ouvrageLie;

    /** Scénario lié */
    private String scenarioLie;

    /** Indique si l'ouvrage est prêt */
    private Boolean pret;

    /** Errata éventuels */
    private String errata;

    /** Notes internes */
    private String notes;

    /** Liste des scénarios contenus dans l'ouvrage */
    private List<String> scenariosContenus;

    /** Liste des autres ouvrages appartenant à la même gamme */
    private List<String> autresOuvragesGamme;

    /** URL de l'image associée */
    private String imageUrl;

    /** Identifiant du propriétaire de l'ouvrage */
    private Long ownerId;

    /** Nom de la gamme associée */
    private String gammeNom;

    /** Constructeur vide requis pour la sérialisation/desérialisation JSON */
    public OuvrageDTO() {
    }

    // --- Getters et Setters avec Javadoc ---

    /** @return l'identifiant de l'ouvrage */
    public Long getId() {
        return id;
    }

    /** @param id nouvel identifiant de l'ouvrage */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return le titre de l'ouvrage */
    public String getTitre() {
        return titre;
    }

    /** @param titre nouveau titre de l'ouvrage */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /** @return la description de l'ouvrage */
    public String getDescription() {
        return description;
    }

    /** @param description nouvelle description de l'ouvrage */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return l'identifiant de la gamme associée */
    public Long getGammeId() {
        return gammeId;
    }

    /** @param gammeId nouvel identifiant de la gamme */
    public void setGammeId(Long gammeId) {
        this.gammeId = gammeId;
    }

    /** @return la version de l'ouvrage */
    public String getVersion() {
        return version;
    }

    /** @param version nouvelle version de l'ouvrage */
    public void setVersion(String version) {
        this.version = version;
    }

    /** @return le type de l'ouvrage */
    public String getTypeOuvrage() {
        return typeOuvrage;
    }

    /** @param typeOuvrage nouveau type de l'ouvrage */
    public void setTypeOuvrage(String typeOuvrage) {
        this.typeOuvrage = typeOuvrage;
    }

    /** @return la date de publication */
    public LocalDate getDatePublication() {
        return datePublication;
    }

    /** @param datePublication nouvelle date de publication */
    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    /** @return la langue de l'ouvrage */
    public String getLangue() {
        return langue;
    }

    /** @param langue nouvelle langue de l'ouvrage */
    public void setLangue(String langue) {
        this.langue = langue;
    }

    /** @return l'éditeur */
    public String getEditeur() {
        return editeur;
    }

    /** @param editeur nouvel éditeur */
    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    /** @return l'état physique de l'ouvrage */
    public String getEtat() {
        return etat;
    }

    /** @param etat nouvel état physique de l'ouvrage */
    public void setEtat(String etat) {
        this.etat = etat;
    }

    /** @return l'ISBN */
    public String getIsbn() {
        return isbn;
    }

    /** @param isbn nouvel ISBN */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /** @return l'ouvrage lié */
    public String getOuvrageLie() {
        return ouvrageLie;
    }

    /** @param ouvrageLie nouvel ouvrage lié */
    public void setOuvrageLie(String ouvrageLie) {
        this.ouvrageLie = ouvrageLie;
    }

    /** @return le scénario lié */
    public String getScenarioLie() {
        return scenarioLie;
    }

    /** @param scenarioLie nouveau scénario lié */
    public void setScenarioLie(String scenarioLie) {
        this.scenarioLie = scenarioLie;
    }

    /** @return true si l'ouvrage est prêt */
    public Boolean getPret() {
        return pret;
    }

    /** @param pret nouvel état de prêt */
    public void setPret(Boolean pret) {
        this.pret = pret;
    }

    /** @return les errata éventuels */
    public String getErrata() {
        return errata;
    }

    /** @param errata nouveaux errata */
    public void setErrata(String errata) {
        this.errata = errata;
    }

    /** @return les notes internes */
    public String getNotes() {
        return notes;
    }

    /** @param notes nouvelles notes internes */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /** @return la liste des scénarios contenus */
    public List<String> getScenariosContenus() {
        return scenariosContenus;
    }

    /** @param scenariosContenus nouvelle liste de scénarios contenus */
    public void setScenariosContenus(List<String> scenariosContenus) {
        this.scenariosContenus = scenariosContenus;
    }

    /** @return la liste des autres ouvrages de la même gamme */
    public List<String> getAutresOuvragesGamme() {
        return autresOuvragesGamme;
    }

    /** @param autresOuvragesGamme nouvelle liste des autres ouvrages de la même gamme */
    public void setAutresOuvragesGamme(List<String> autresOuvragesGamme) {
        this.autresOuvragesGamme = autresOuvragesGamme;
    }

    /** @return l'identifiant du propriétaire */
    public Long getOwnerId() {
        return ownerId;
    }

    /** @param ownerId nouvel identifiant du propriétaire */
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    /** @return l'URL de l'image associée */
    public String getImageUrl() {
        return imageUrl;
    }

    /** @param imageUrl nouvelle URL de l'image */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /** @return le nom de la gamme associée */
    public String getGammeNom() {
        return gammeNom;
    }

    /** @param gammeNom nouveau nom de la gamme */
    public void setGammeNom(String gammeNom) {
        this.gammeNom = gammeNom;
    }
}
