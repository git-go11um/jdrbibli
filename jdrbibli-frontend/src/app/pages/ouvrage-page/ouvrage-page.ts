import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ouvrage-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ouvrage-page.html',
  styleUrls: ['./ouvrage-page.scss']
})
export class OuvragePage implements OnInit {
  ouvrage = {
    id: 1,
    titre: 'Livre de base',
    version: '1.0',
    type: 'Livre papier',
    datePublication: '2025-07', // format YYYY-MM
    langue: 'Français',
    editeur: 'Games Workshop',
    etat: 'Neuf',
    eanIsbn: '1234567890123',
    lieA: '',
    scenarioLie: '',
    pret: '',
    errata: '',
    notes: '',
    scenarios: '',
     liens: ['']
  };

  gamme = {
    id: 1,
    nom: 'Warhammer'
  };

  typeOptions = ['Livre papier', 'Ecran', 'Carte'];
  langueOptions = ['Français', 'Anglais', 'Autre'];
  etatOptions = ['Neuf', 'Bon', 'Moyen', 'Mauvais'];

  autresOuvrages = [
    { id: 2, titre: 'Extension 1' },
    { id: 3, titre: 'Bestiaire' }
  ];

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    // récupérer id et charger depuis backend plus tard
    const id = Number(this.route.snapshot.paramMap.get('id'));
    // pour l’instant, on garde le mock
  }

  goToGamme() {
    this.router.navigate(['/gamme', this.gamme.id]);
  }

  goToOuvrage(id: number) {
    this.router.navigate(['/ouvrage', id]);
  }

  ajouterLien() {
    this.ouvrage.liens.push('');
  }

}
