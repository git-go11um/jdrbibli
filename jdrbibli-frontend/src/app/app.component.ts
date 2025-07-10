import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { AuthService } from './services/auth.service';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent, CommonModule],
  template: `
    <app-header *ngIf="showHeader"></app-header>
    <router-outlet></router-outlet>
    <app-footer></app-footer>
  `,
})
export class AppComponent {
  showHeader = false;

  constructor(private router: Router, private authService: AuthService) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      const url = this.router.url;
      this.showHeader = this.authService.isLoggedIn() && url !== '/'; // ou autre chemin public
    });
  }
}
