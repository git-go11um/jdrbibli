import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';



import { OuvrageService, OuvrageDTO } from '../../services/ouvrage.service';

@Component({
  selector: 'app-creation-edition-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './creation-edition-page.html',
  styleUrls: ['./creation-edition-page.scss']
})
export class CreationEditionPage implements OnInit {

  ouvrage: OuvrageDTO | undefined;

  // On garde une Date locale pour le datepicker
  datePublication: Date | null = null;

  scenariosContenusString: string = '';
  liensMediasString: string = '';

  typeOptions = ['Livre papier', 'Ecran', 'Carte'];
  langueOptions = ['Français', 'Anglais', 'Autre'];
  etatOptions = ['Neuf', 'Bon', 'Moyen', 'Mauvais'];

  loading = false;
  isSaving = false;
  isCreationMode = false;
  isSavingAttempted = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ouvrageService: OuvrageService
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : null;

    const gammeIdParam = this.route.snapshot.queryParamMap.get('gammeId');
    const gammeId = gammeIdParam ? Number(gammeIdParam) : null;

    if (id && id > 0) {
      this.isCreationMode = false;
      this.loadOuvrage(id);
    } else {
      this.isCreationMode = true;
      this.ouvrage = this.createEmptyOuvrage();
      if (gammeId) this.ouvrage.gammeId = gammeId; // ✅ Préremplissage
    }
  }

  private createEmptyOuvrage(): OuvrageDTO {
    return {
      id: undefined,
      titre: '',
      description: '',
      version: '',
      typeOuvrage: '',
      datePublication: '', // backend attend un string
      langue: '',
      editeur: '',
      etat: '',
      isbn: '',
      ouvrageLie: '',
      scenarioLie: '',
      pret: false,
      errata: '',
      notes: '',
      scenariosContenus: [],
      autresOuvragesGamme: [],
      liensMedias: [],
      gammeId: null,
      ownerPseudo: undefined,
      ownerId: undefined,
    };
  }

  loadOuvrage(id: number): void {
    this.loading = true;
    this.ouvrageService.getById(id).subscribe({
      next: (data) => {
        this.ouvrage = data;

        // conversion datePublication string -> Date
        if (this.ouvrage.datePublication) {
          this.datePublication = new Date(this.ouvrage.datePublication);
        }

        this.scenariosContenusString = this.ouvrage.scenariosContenus?.join(', ') ?? '';
        this.liensMediasString = this.ouvrage.liensMedias?.join(', ') ?? '';
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement ouvrage :', err);
        this.loading = false;
      }
    });
  }

  ajouterLien(): void {
    if (!this.ouvrage) return;
    if (!this.ouvrage.liensMedias) this.ouvrage.liensMedias = [];
    this.ouvrage.liensMedias.push('');
  }

  updateArraysFromStrings(): void {
    if (!this.ouvrage) return;
    this.ouvrage.scenariosContenus = this.stringToArray(this.scenariosContenusString);
    this.ouvrage.liensMedias = this.stringToArray(this.liensMediasString);
  }

  private stringToArray(value: string): string[] {
    return value ? value.split(',').map(s => s.trim()).filter(s => s.length > 0) : [];
  }

  sauvegarderOuvrage(): void {
    if (!this.ouvrage || this.isSaving) return;

    this.isSavingAttempted = true;

    if (!this.ouvrage.titre || this.ouvrage.titre.trim().length === 0) {
      alert('Le titre de l’ouvrage est obligatoire !');
      return;
    }

    this.isSaving = true;
    this.updateArraysFromStrings();

    if (this.ouvrage.id) {
      this.ouvrageService.update(this.ouvrage.id, this.ouvrage).subscribe({
        next: () => {
          alert('Modifications sauvegardées !');
          this.isSaving = false;
        },
        error: (err) => {
          alert('Erreur lors de la sauvegarde');
          console.error(err);
          this.isSaving = false;
        }
      });
    } else {
      this.ouvrageService.create(this.ouvrage).subscribe({
        next: (created) => {
          alert('Ouvrage créé !');
          if (created.gammeId) {
            this.router.navigate(['/gamme', created.gammeId]);
          } else {
            this.router.navigate(['/ludotheque']);
          }
          this.isSaving = false;
        },
        error: (err) => {
          alert('Erreur lors de la création');
          console.error(err);
          this.isSaving = false;
        }
      });
    }
  }


}
