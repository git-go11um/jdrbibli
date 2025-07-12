import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { GammeService, GammeDTO } from '../../services/gamme.service';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-ludotheque-page',
  templateUrl: './ludotheque-page.html',
  styleUrl: './ludotheque-page.scss',
  imports: [CommonModule, FormsModule]

})
export class LudothequePage implements OnInit {
  gammes: GammeDTO[] = [];
  columns: GammeDTO[][] = [];  // pour stocker les colonnes dynamiques
  selectedGammeId: number | null = null;
  newGammeName: string = '';

  constructor(
    private gammeService: GammeService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadGammes();
  }

  /** Charge les gammes depuis le backend et trie puis divise en colonnes */
  loadGammes(): void {
    this.gammeService.getAll().subscribe({
      next: (data) => {
        this.gammes = data.sort((a, b) => a.nom.localeCompare(b.nom));
        this.splitGammesIntoColumns();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des gammes:', err);
      }
    });
  }

  /** Divise la liste des gammes triées en 3 colonnes */
  private splitGammesIntoColumns(): void {
    const numberOfColumns = 3;
    this.columns = Array.from({ length: numberOfColumns }, () => []);

    this.gammes.forEach((gamme, index) => {
      this.columns[index % numberOfColumns].push(gamme);
    });
  }

  /** Navigue vers la page d'une gamme */
  goToGamme(id: number): void {
    this.selectedGammeId = id;
    this.router.navigate(['/gamme', id]);
  }

  /** Ajoute une nouvelle gamme (backend) et recharge */
  ajouterGamme(): void {
    if (!this.newGammeName.trim()) {
      alert('Veuillez saisir un nom pour la gamme.');
      return;
    }
    const nouvelleGamme: GammeDTO = { nom: this.newGammeName.trim(), description: '' };
    this.gammeService.create(nouvelleGamme).subscribe({
      next: () => {
        this.newGammeName = ''; // reset input
        this.loadGammes();
      },
      error: (err) => {
        console.error('Erreur lors de l\'ajout de la gamme:', err);
      }
    });
  }



  /** Supprime la gamme sélectionnée (backend) et recharge */
  supprimerGamme(): void {
    if (this.selectedGammeId !== null) {
      this.gammeService.delete(this.selectedGammeId, true).subscribe({
        next: () => {
          this.selectedGammeId = null;
          this.loadGammes();
        },
        error: (err) => {
          console.error('Erreur lors de la suppression de la gamme:', err);
          alert('Erreur lors de la suppression de la gamme.');
        }
      });
    } else {
      alert('Sélectionnez d\'abord une gamme pour la supprimer.');
    }
  }
}
