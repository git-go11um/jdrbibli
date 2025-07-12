import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

// Définition locale de l'interface Gamme
interface Gamme {
  id: number;
  nom: string;
}

@Component({
  selector: 'app-ludotheque-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ludotheque-page.html',
  styleUrls: ['./ludotheque-page.scss']
})
export class LudothequePage implements OnInit {
  gammes: Gamme[] = [];  // <-- précise bien le type ici

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.gammes = [
      { id: 1, nom: 'Warhammer' },
      { id: 2, nom: "L'appel de Cthulhu" }
    ];

    this.gammes.sort((a, b) => a.nom.localeCompare(b.nom));
  }

  goToGamme(id: number) {
    this.router.navigate(['/gamme', id]);
  }
}
