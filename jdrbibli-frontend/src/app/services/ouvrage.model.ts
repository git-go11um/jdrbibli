export class Ouvrage {
    id?: number;
    nom: string; // ou titre, selon ton modèle
    description: string;
    version: string;
    typeOuvrage: string;
    datePublication: string;
    langue: string;
    editeur: string;
    etat: string;
    isbn: string;
    ouvrageLie: string;
    scenarioLie: string;
    pret: boolean;
    errata: string;
    notes: string;
    scenariosContenus: string[];
    autresOuvragesGamme: string[];
    gammeId?: number | null;
    ownerPseudo?: string;
    ownerId?: number;
    imageUrl?: string;
    gammeNom?: string;

    constructor(dto: any) {
        this.id = dto.id;
        this.nom = dto.titre; // map "titre" du DTO vers "nom" du modèle
        this.description = dto.description;
        this.version = dto.version;
        this.typeOuvrage = dto.typeOuvrage;
        this.datePublication = dto.datePublication;
        this.langue = dto.langue;
        this.editeur = dto.editeur;
        this.etat = dto.etat;
        this.isbn = dto.isbn;
        this.ouvrageLie = dto.ouvrageLie;
        this.scenarioLie = dto.scenarioLie;
        this.pret = dto.pret;
        this.errata = dto.errata;
        this.notes = dto.notes;
        this.scenariosContenus = dto.scenariosContenus;
        this.autresOuvragesGamme = dto.autresOuvragesGamme;
        this.gammeId = dto.gammeId;
        this.ownerPseudo = dto.ownerPseudo;
        this.ownerId = dto.ownerId;
        this.imageUrl = dto.imageUrl;
        this.gammeNom = dto.gammeNom;
    }
}
