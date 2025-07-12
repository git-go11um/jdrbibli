import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { OuvrageService, OuvrageDTO } from '../../services/ouvrage.service';
import { GammeService, GammeDTO } from '../../services/gamme.service';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-gamme-page',
  templateUrl: './gamme-page.html',
  styleUrls: ['./gamme-page.scss'],
  imports: [CommonModule, FormsModule]
})
export class GammePage implements OnInit {
  gamme: GammeDTO | undefined;
  ouvrages: OuvrageDTO[] = []; // Tableau d'ouvrages
  newOuvrageName: string = '';
  newOuvrageDescription: string = '';
  newOuvrageDatePublication: Date | null = null; // Date au format Date

  constructor(
    private gammeService: GammeService,
    private ouvrageService: OuvrageService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    const gammeId = Number(this.route.snapshot.paramMap.get('id')); // Récupère l'ID de la gamme dans l'URL

    if (!gammeId) {
      console.error("ID de la gamme invalide");
      return;  // Si l'ID de la gamme est invalide, on arrête l'exécution.
    }

    this.loadGamme(gammeId);
    this.loadOuvrages(gammeId); // Charger les ouvrages pour la gamme spécifiée
  }

  /** Charge les détails d'une gamme */
  loadGamme(gammeId: number): void {
    this.gammeService.getById(gammeId).subscribe({
      next: (data) => {
        this.gamme = data;
      },
      error: (err) => {
        console.error('Erreur lors du chargement de la gamme:', err);
      }
    });
  }

  /** Charge les ouvrages de cette gamme */
  loadOuvrages(gammeId: number): void {
    // Utilise une méthode qui récupère les ouvrages d'une gamme, pas `getById`
    this.ouvrageService.getByGamme(gammeId).subscribe({
      next: (data: OuvrageDTO[]) => { // Assure-toi que 'data' est bien un tableau
        this.ouvrages = data; // Remplir la liste des ouvrages
      },
      error: (err) => {
        console.error('Erreur lors du chargement des ouvrages:', err);
      }
    });
  }

  /** Ajouter un ouvrage à cette gamme */
  ajouterOuvrage(): void {
    if (!this.newOuvrageName.trim()) {
      alert('Veuillez saisir un titre pour l\'ouvrage.');
      return;
    }

    // Vérifie si la gamme est définie et si elle a un ID valide
    if (!this.gamme || !this.gamme.id) {
      console.error("ID de la gamme est invalide");
      return;
    }

    // Créer un nouvel ouvrage avec toutes les propriétés
    const nouvelOuvrage: OuvrageDTO = {
      titre: this.newOuvrageName.trim(),
      description: this.newOuvrageDescription,
      gammeId: this.gamme.id, // Assure-toi que la gamme est bien assignée
      version: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      typeOuvrage: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      datePublication: this.newOuvrageDatePublication ? this.newOuvrageDatePublication.toISOString() : '', // Conversion en ISO string
      langue: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      editeur: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      etat: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      isbn: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      ouvrageLie: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      scenarioLie: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      pret: false, // Par défaut, tu peux le laisser à false
      errata: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      notes: '', // Par défaut, tu peux le laisser vide ou récupérer cette info du formulaire
      scenariosContenus: [], // Liste vide par défaut
      autresOuvragesGamme: [], // Liste vide par défaut
      liensMedias: [] // Liste vide par défaut
    };

    this.ouvrageService.create(nouvelOuvrage).subscribe({
      next: () => {
        this.newOuvrageName = ''; // Reset
        this.newOuvrageDescription = ''; // Reset
        this.newOuvrageDatePublication = null; // Reset
        if (this.gamme?.id) {
          this.loadOuvrages(this.gamme.id); // Recharger les ouvrages uniquement si la gamme existe
        }
      },
      error: (err) => {
        console.error('Erreur lors de l\'ajout de l\'ouvrage:', err);
      }
    });
  }

  /** Supprimer un ouvrage */
  supprimerOuvrage(ouvrageId: number): void {
    this.ouvrageService.delete(ouvrageId).subscribe({
      next: () => {
        if (this.gamme && this.gamme.id) {
          this.loadOuvrages(this.gamme.id); // Recharger les ouvrages si la gamme est valide
        }
      },
      error: (err) => {
        console.error('Erreur lors de la suppression de l\'ouvrage:', err);
      }
    });
  }

  /** Modifier un ouvrage (redirection vers la page de l'ouvrage) */
  modifierOuvrage(ouvrageId: number): void {
    this.router.navigate(['/ouvrage', ouvrageId]); // Rediriger vers la page de modification de l'ouvrage
  }
}
