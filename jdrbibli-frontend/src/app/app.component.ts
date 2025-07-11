import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common'; // <-- Assure-toi que CommonModule est bien importé ici
import { FooterComponent } from './components/footer/footer.component';
import { Navbar } from './components/navbar/navbar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, Navbar, FooterComponent],
  template: `
    <app-navbar *ngIf="isLoggedIn"></app-navbar>
    <router-outlet></router-outlet>
    <app-footer></app-footer>
  `,
})
export class AppComponent {
  get isLoggedIn(): boolean {
    const token = localStorage.getItem('jwt');
    console.log('Token is:', token);  // Vérifier ce que ça renvoie
    return !!token;
  }
}
