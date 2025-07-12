import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

interface Ouvrage {
  id: number;
  titre: string;
}

interface Gamme {
  id: number;
  nom: string;
  description: string;
  ouvrages: Ouvrage[];
}

@Component({
  selector: 'app-gamme-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gamme-page.html',
  styleUrls: ['./gamme-page.scss']
})
export class GammePage implements OnInit {
  gamme: Gamme | null = null;

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    // Mock d'une gamme avec ouvrages
    if (id === 1) { // Warhammer
      this.gamme = {
        id: 1,
        nom: 'Warhammer',
        description: 'Jeu de rôle médiévale fantastique.',
        ouvrages: [
          { id: 1, titre: 'Livre de base' },
          { id: 2, titre: 'Extension 1' }
        ]
      };
    } else if (id === 2) { // L'appel de Cthulhu
      this.gamme = {
        id: 2,
        nom: "L'appel de Cthulhu",
        description: 'Jeu d’horreur et enquête basé sur Lovecraft.',
        ouvrages: [
          { id: 3, titre: 'Livre de base' }
        ]
      };
    } else {
      // Si gamme inconnue, revenir à la ludothèque
      this.router.navigate(['/ludotheque']);
    }
  }

  goToOuvrage(id: number) {
    this.router.navigate(['/ouvrage', id]);
  }
}
