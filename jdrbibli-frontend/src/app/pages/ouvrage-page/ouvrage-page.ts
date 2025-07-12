import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

interface Ouvrage {
  id: number;
  titre: string;
  description: string;
}

@Component({
  selector: 'app-ouvrage-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ouvrage-page.html',
  styleUrls: ['./ouvrage-page.scss']
})
export class OuvragePage implements OnInit {
  ouvrage: Ouvrage | null = null;

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    // Mock selon l'id
    if (id === 1) {
      this.ouvrage = {
        id: 1,
        titre: 'Livre de base',
        description: 'Description complète du livre de base de Warhammer.'
      };
    } else if (id === 2) {
      this.ouvrage = {
        id: 2,
        titre: 'Extension 1',
        description: 'Première extension du jeu Warhammer.'
      };
    } else if (id === 3) {
      this.ouvrage = {
        id: 3,
        titre: "Livre de base",
        description: "Description du livre de base de L'appel de Cthulhu."
      };
    } else {
      // Si ouvrage inconnu, retourne à la ludothèque
      this.router.navigate(['/ludotheque']);
    }
  }

  goBack() {
    this.router.navigate(['/gamme', this.ouvrage?.id]); // On pourrait améliorer en récupérant la gamme réelle
  }
}
