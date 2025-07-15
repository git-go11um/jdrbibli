import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service'; // Assure-toi que le chemin est correct

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink], // Vérifie que RouterLink est bien importé
  template: `
    <nav class="navbar">
      <a routerLink="/home-connected" routerLinkActive="active">Accueil</a>
      <a routerLink="/profil-utilisateur" routerLinkActive="active">Profil</a>
      <a routerLink="/ludotheque" routerLinkActive="active">Ludothèque</a>
      <button (click)="logout()">Déconnexion</button>
    </nav>
  `,
  styleUrls: ['./navbar.scss']
})
export class Navbar {

  constructor(private authService: AuthService, private router: Router) { }

  logout() {
    this.authService.logout();          // Supprime le token
    this.router.navigate(['/']);        // Redirige vers la home publique
  }
}
