import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
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
  // Getter qui vérifie à chaque détection de changement si un token est présent
  get isLoggedIn(): boolean {
    const token = localStorage.getItem('jwt');
    console.log('Token is:', token);
    return !!token;
  }
}
