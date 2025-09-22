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

  typeOptions = ['Livre papier', 'Ecran', 'Carte'];
  langueOptions = ['Français', 'Anglais', 'Autre'];
  etatOptions = ['Neuf', 'Bon', 'Moyen', 'Mauvais'];
  imageFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;

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
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement ouvrage :', err);
        this.loading = false;
      }
    });
  }

  updateArraysFromStrings(): void {
    if (!this.ouvrage) return;
    this.ouvrage.scenariosContenus = this.stringToArray(this.scenariosContenusString);
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
    this.updateArraysFromStrings(); // Vérifie que cette ligne est appelée

    console.log('Données à sauvegarder :', this.ouvrage); // Affiche les données avant l'envoi

    const saveOuvrage = () => {
      if (this.ouvrage!.id) {
        // --- EDITION ---
        this.ouvrageService.update(this.ouvrage!.id, this.ouvrage!).subscribe({
          next: (updated) => {
            alert('Modifications sauvegardées !');
            this.isSaving = false;

            if (updated.gammeId) {
              this.router.navigate(['/gamme', updated.gammeId]);
            }
          },
          error: (err) => {
            alert('Erreur lors de la sauvegarde');
            console.error(err);
            this.isSaving = false;
          }
        });
      } else {
        // --- CREATION ---
        this.ouvrageService.create(this.ouvrage!).subscribe({
          next: (created) => {
            alert('Ouvrage créé !');
            this.isSaving = false;

            if (created.gammeId) {
              this.router.navigate(['/gamme', created.gammeId]);
            } else {
              this.router.navigate(['/ludotheque']);
            }
          },
          error: (err) => {
            alert('Erreur lors de la création');
            console.error(err);
            this.isSaving = false;
          }
        });
      }
    };

    // Si une image a été sélectionnée, l’uploader d’abord
    if (this.imageFile) {
      this.ouvrageService.uploadImage(this.imageFile).subscribe({
        next: (url) => {
          this.ouvrage!.imageUrl = url;
          saveOuvrage();
        },
        error: (err) => {
          alert('Erreur lors de l’upload de l’image');
          console.error(err);
          this.isSaving = false;
        }
      });
    } else {
      saveOuvrage();
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    this.imageFile = input.files[0];

    // Aperçu
    const reader = new FileReader();
    reader.onload = e => this.imagePreview = reader.result;
    reader.readAsDataURL(this.imageFile);
  }

}
