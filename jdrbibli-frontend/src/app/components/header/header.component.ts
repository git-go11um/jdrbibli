import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink],
  template: `
    <header class="app-header">
      <nav>
        <a routerLink="/" class="logo">JdrBibli</a>
        <a routerLink="/home-public">Accueil Public</a>
        <a routerLink="/login">Se connecter</a>
        <a routerLink="/register">Créer un compte</a>
      </nav>
    </header>
  `,
  styles: [`
    .app-header {
      background-color: #3f51b5;
      padding: 1rem;
      color: white;
    }
    nav {
      display: flex;
      gap: 1.5rem;
      align-items: center;
    }
    a {
      color: white;
      text-decoration: none;
      font-weight: 600;
      font-size: 1.1rem;
    }
    a.logo {
      font-weight: 900;
      font-size: 1.4rem;
    }
    a:hover {
      text-decoration: underline;
    }
  `]
})
export class HeaderComponent {}
