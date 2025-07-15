import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { OuvrageService, OuvrageDTO } from '../../services/ouvrage.service';
import { GammeService, GammeDTO } from '../../services/gamme.service';

@Component({
  selector: 'app-ouvrage-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ouvrage-page.html',
  styleUrls: ['./ouvrage-page.scss']
})
export class OuvragePage implements OnInit {

  ouvrage: OuvrageDTO | undefined;
  gamme: GammeDTO | undefined;
  autresOuvrages: OuvrageDTO[] = [];

  scenariosContenusString: string = '';
  autresOuvragesGammeString: string = '';
  liensMediasString: string = '';

  typeOptions = ['Livre papier', 'Ecran', 'Carte'];
  langueOptions = ['Français', 'Anglais', 'Autre'];
  etatOptions = ['Neuf', 'Bon', 'Moyen', 'Mauvais'];

  loading = false;
  isSaving = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ouvrageService: OuvrageService,
    private gammeService: GammeService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id > 0) {
      this.loadOuvrage(id);
    } else {
      console.error('ID ouvrage invalide');
    }
  }

  loadOuvrage(id: number): void {
    this.loading = true;
    this.ouvrageService.getById(id).subscribe({
      next: (data) => {
        this.ouvrage = data;

        // Initialisation des strings pour la gestion des champs textarea/input
        this.scenariosContenusString = this.ouvrage.scenariosContenus?.join(', ') ?? '';
        this.autresOuvragesGammeString = this.ouvrage.autresOuvragesGamme?.join(', ') ?? '';
        this.liensMediasString = this.ouvrage.liensMedias?.join(', ') ?? '';

        // Charger la gamme et les autres ouvrages dans la gamme
        if (this.ouvrage.gammeId) {
          this.loadGammeEtAutresOuvrages(this.ouvrage.gammeId, this.ouvrage.id!);
        } else {
          this.loading = false;
          console.warn('Aucun gammeId pour cet ouvrage');
        }
      },
      error: (err) => {
        console.error('Erreur chargement ouvrage :', err);
        this.loading = false;
      }
    });
  }

  loadGammeEtAutresOuvrages(gammeId: number, ouvrageId: number): void {
    this.gammeService.getById(gammeId).subscribe({
      next: (gammeData) => {
        this.gamme = gammeData;

        // Récupérer les ouvrages de cette gamme, en excluant l'ouvrage courant
        this.ouvrageService.getByGamme(gammeId).subscribe({
          next: (ouvrages) => {
            this.autresOuvrages = ouvrages.filter(o => o.id !== ouvrageId);
            this.loading = false;
          },
          error: (err) => {
            console.error('Erreur chargement autres ouvrages de la gamme:', err);
            this.loading = false;
          }
        });
      },
      error: (err) => {
        console.error('Erreur chargement gamme:', err);
        this.loading = false;
      }
    });
  }

  goToGamme(): void {
    if (this.gamme?.id) {
      this.router.navigate(['/gamme', this.gamme.id]);
    }
  }

  goToOuvrage(id: number): void {
    this.router.navigate(['/ouvrage', id]);
  }

  ajouterLien(): void {
    if (!this.ouvrage) return;
    if (!this.ouvrage.liensMedias) {
      this.ouvrage.liensMedias = [];
    }
    this.ouvrage.liensMedias.push('');
  }

  updateArraysFromStrings(): void {
    if (!this.ouvrage) return;

    this.ouvrage.scenariosContenus = this.stringToArray(this.scenariosContenusString);
    this.ouvrage.autresOuvragesGamme = this.stringToArray(this.autresOuvragesGammeString);
    this.ouvrage.liensMedias = this.stringToArray(this.liensMediasString);
  }

  private stringToArray(value: string): string[] {
    return value
      ? value.split(',')
          .map(s => s.trim())
          .filter(s => s.length > 0)
      : [];
  }

  sauvegarderOuvrage(): void {
    if (!this.ouvrage || this.isSaving) return;

    this.isSaving = true;
    this.updateArraysFromStrings();

    this.ouvrageService.update(this.ouvrage.id!, this.ouvrage).subscribe({
      next: () => {
        alert('Modifications sauvegardées !');
        this.isSaving = false;
      },
      error: (err) => {
        alert('Erreur lors de la sauvegarde, veuillez réessayer.');
        this.isSaving = false;
        console.error('Erreur lors de la mise à jour:', err);
      }
    });
  }

}
