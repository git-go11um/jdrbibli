import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router'; // Import du RouterModule
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
  imports: [CommonModule, FormsModule, RouterModule]
})
export class GammePage implements OnInit {
  gamme: GammeDTO | undefined;
  ouvrages: OuvrageDTO[] = [];

  // Mode ajout ou édition ?
  afficherFormulaireAjout: boolean = false;
  enEdition: boolean = false;

  // Objet pour le formulaire (ajout ou édition)
  currentOuvrage: any = this.getEmptyOuvrage();

  constructor(
    private gammeService: GammeService,
    private ouvrageService: OuvrageService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    const gammeId = Number(this.route.snapshot.paramMap.get('id'));
    if (gammeId > 0) {
      this.loadGamme(gammeId);
      this.loadOuvrages(gammeId);
    } else {
      console.error('ID de gamme invalide ou manquant');
    }
  }

  loadGamme(gammeId: number): void {
    this.gammeService.getById(gammeId).subscribe({
      next: (data) => this.gamme = data,
      error: (err) => console.error('Erreur lors du chargement de la gamme:', err)
    });
  }

  loadOuvrages(gammeId: number): void {
    this.ouvrageService.getByGamme(gammeId).subscribe({
      next: (data) => this.ouvrages = data,
      error: (err) => console.error('Erreur lors du chargement des ouvrages:', err)
    });
  }

  // Crée un objet vierge pour le formulaire
  private getEmptyOuvrage() {
    return {
      id: null,
      titre: '',
      description: '',
      version: '',
      typeOuvrage: '',
      datePublication: '',
      langue: '',
      editeur: '',
      etat: '',
      isbn: '',
      ouvrageLie: '',
      scenarioLie: '',
      pret: false,
      errata: '',
      notes: '',
      scenariosContenusString: '',
      autresOuvragesGammeString: '',
      liensMediasString: ''
    };
  }

  // Ouvre le formulaire en mode ajout
  ouvrirFormulaireAjout() {
    this.enEdition = false;
    this.currentOuvrage = this.getEmptyOuvrage();
    this.afficherFormulaireAjout = true;
  }

  // Ouvre le formulaire en mode édition avec chargement des données
  ouvrirFormulaireEdition(ouvrageId: number) {
    this.ouvrageService.getById(ouvrageId).subscribe({
      next: (data) => {
        this.enEdition = true;
        this.currentOuvrage = {
          ...data,
          scenariosContenusString: data.scenariosContenus?.join(', ') || '',
          autresOuvragesGammeString: data.autresOuvragesGamme?.join(', ') || '',
          liensMediasString: data.liensMedias?.join(', ') || '',
          datePublication: data.datePublication ? data.datePublication.substring(0, 10) : '' // si date ISO string
        };
        this.afficherFormulaireAjout = true;
      },
      error: (err) => console.error('Erreur chargement ouvrage pour édition:', err)
    });
  }

  // Soumet le formulaire (ajout ou modification)
  submitFormulaire() {
    const dto: OuvrageDTO = {
      id: this.currentOuvrage.id,
      titre: this.currentOuvrage.titre.trim(),
      description: this.currentOuvrage.description,
      gammeId: this.gamme?.id!,
      version: this.currentOuvrage.version,
      typeOuvrage: this.currentOuvrage.typeOuvrage,
      datePublication: this.currentOuvrage.datePublication,
      langue: this.currentOuvrage.langue,
      editeur: this.currentOuvrage.editeur,
      etat: this.currentOuvrage.etat,
      isbn: this.currentOuvrage.isbn,
      ouvrageLie: this.currentOuvrage.ouvrageLie,
      scenarioLie: this.currentOuvrage.scenarioLie,
      pret: this.currentOuvrage.pret,
      errata: this.currentOuvrage.errata,
      notes: this.currentOuvrage.notes,
      scenariosContenus: this.splitStringToArray(this.currentOuvrage.scenariosContenusString),
      autresOuvragesGamme: this.splitStringToArray(this.currentOuvrage.autresOuvragesGammeString),
      liensMedias: this.splitStringToArray(this.currentOuvrage.liensMediasString),
    };

    if (this.enEdition && dto.id != null) {
      this.ouvrageService.update(dto.id, dto).subscribe({
        next: () => {
          console.log('Ouvrage modifié avec succès');
          this.loadOuvrages(this.gamme!.id!);
          this.afficherFormulaireAjout = false;
          this.currentOuvrage = this.getEmptyOuvrage();
        },
        error: (err) => console.error('Erreur lors de la modification:', err)
      });
    } else {
      // ajout
      if (!dto.titre) {
        alert('Le titre est obligatoire');
        return;
      }
      this.ouvrageService.create(dto).subscribe({
        next: () => {
          console.log('Ouvrage ajouté avec succès');
          this.loadOuvrages(this.gamme!.id!);
          this.afficherFormulaireAjout = false;
          this.currentOuvrage = this.getEmptyOuvrage();
        },
        error: (err) => console.error('Erreur lors de la création:', err)
      });
    }
  }

  supprimerOuvrage(ouvrageId: number): void {
    if (!confirm('Voulez-vous vraiment supprimer cet ouvrage ?')) return;
    this.ouvrageService.delete(ouvrageId).subscribe({
      next: () => this.loadOuvrages(this.gamme!.id!),
      error: (err) => console.error('Erreur lors de la suppression:', err)
    });
  }

  modifierOuvrage(ouvrageId: number): void {
    this.ouvrirFormulaireEdition(ouvrageId);
  }

  toggleFormulaireAjout(): void {
    if (this.afficherFormulaireAjout) {
      this.afficherFormulaireAjout = false;
      this.currentOuvrage = this.getEmptyOuvrage();
      this.enEdition = false;
    } else {
      this.ouvrirFormulaireAjout();
    }
  }

  private splitStringToArray(value: string): string[] {
    return value ? value.split(',').map(s => s.trim()).filter(s => s.length > 0) : [];
  }

  ouvrirPageOuvrage(ouvrageId: number): void {
    this.router.navigate(['/ouvrage', ouvrageId]);
  }

}
