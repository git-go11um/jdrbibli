import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home-connected',
  standalone: true,
  templateUrl: './home-connected.html',
  styleUrl: './home-connected.scss'
})
export class HomeConnected {
  constructor(private authService: AuthService, private router: Router) {}

  logout() {
    this.authService.logout();          // Supprime le token
    this.router.navigate(['/']);        // Redirige vers la home publique (ou login)
  }
}
