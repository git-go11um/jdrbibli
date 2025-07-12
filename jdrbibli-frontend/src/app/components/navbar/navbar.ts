import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  template: `
    <nav class="navbar">
      <a routerLink="/home-connected">Accueil</a>
      <a routerLink="/profil-utilisateur">Profil</a>
      <a routerLink="/ludotheque">Ludothèque</a>
      <button>Déconnexion</button>
    </nav>
  `,
  styleUrls: ['./navbar.scss']
})
export class Navbar { }
