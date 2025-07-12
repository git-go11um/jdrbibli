import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-ludotheque-page',
  templateUrl: './ludotheque-page.html',
  styleUrl: './ludotheque-page.scss',
  imports: [CommonModule]
})
export class LudothequePage {
  gammes = [
    { id: 1, nom: 'Warhammer', description: '' },
    { id: 2, nom: "L'appel de Cthulhu", description: '' }
  ];

  selectedGammeId: number | null = null;  // pour savoir quelle gamme est sélectionnée

  constructor(private router: Router) { }


  ngOnInit() {
    this.trierGammes();
  }

  goToGamme(id: number) {
    this.selectedGammeId = id;  // on sélectionne en même temps
    this.router.navigate(['/gamme', id]);
  }

  ajouterGamme() {
    const newId = Math.max(...this.gammes.map(g => g.id)) + 1;
    this.gammes.push({ id: newId, nom: 'Nouvelle gamme', description: '' });
    this.trierGammes();
  }


  supprimerGamme() {
    if (this.selectedGammeId !== null) {
      this.gammes = this.gammes.filter(g => g.id !== this.selectedGammeId);
      this.selectedGammeId = null;
      this.trierGammes();
    } else {
      alert('Sélectionnez d\'abord une gamme pour la supprimer.');
    }
  }


  trierGammes() {
    this.gammes.sort((a, b) => a.nom.localeCompare(b.nom));
  }


}
