import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { GammeService, GammeDTO } from '../../services/gamme.service';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-ludotheque-page',
  templateUrl: './ludotheque-page.html',
  styleUrls: ['./ludotheque-page.scss'],
  imports: [CommonModule, FormsModule]
})
export class LudothequePage implements OnInit {
  gammes: GammeDTO[] = [];
  newGammeName: string = '';

  constructor(
    private gammeService: GammeService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadGammes();
  }

  /** Charge les gammes depuis le backend */
  loadGammes(): void {
    this.gammeService.getAll().subscribe({
      next: (data) => {
        this.gammes = data.sort((a, b) => a.nom.localeCompare(b.nom));
      },
      error: (err) => {
        console.error('Erreur lors du chargement des gammes:', err);
      }
    });
  }

  /** Ajoute une nouvelle gamme (backend) */
  ajouterGamme(): void {
    if (!this.newGammeName.trim()) {
      alert('Veuillez saisir un nom pour la gamme.');
      return;
    }
    const nouvelleGamme: GammeDTO = { nom: this.newGammeName.trim(), description: '' };
    this.gammeService.create(nouvelleGamme).subscribe({
      next: () => {
        this.newGammeName = ''; // Reset input
        this.loadGammes(); // Recharge la liste des gammes
      },
      error: (err) => {
        console.error('Erreur lors de l\'ajout de la gamme:', err);
      }
    });
  }

  /** Supprime une gamme */
  supprimerGamme(gammeId: number): void {
    this.gammeService.delete(gammeId).subscribe({
      next: () => {
        this.loadGammes(); // Recharge la liste des gammes
      },
      error: (err) => {
        console.error('Erreur lors de la suppression de la gamme:', err);
      }
    });
  }

  /** Modifie le nom d'une gamme (fonctionnalité à ajouter après) */
  modifierNomGamme(gammeId: number): void {
    const newName = prompt("Entrez le nouveau nom de la gamme:");
    if (newName && newName.trim()) {
      const updatedGamme: GammeDTO = { nom: newName.trim(), description: '' };
      this.gammeService.update(gammeId, updatedGamme).subscribe({
        next: () => {
          this.loadGammes(); // Recharge la liste des gammes
        },
        error: (err) => {
          console.error('Erreur lors de la modification du nom de la gamme:', err);
        }
      });
    }
  }

  /** Navigue vers la page de la gamme */
  goToGamme(id: number): void {
    this.router.navigate(['/gamme', id]); // Redirige vers la page de la gamme spécifique
  }
}
