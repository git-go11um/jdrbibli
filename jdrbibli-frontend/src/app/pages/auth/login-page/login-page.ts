import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [FormsModule, RouterLink, NgIf],
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.scss']
})
export class LoginPage {
  pseudo = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    this.errorMessage = '';
    console.log('Tentative de login', this.pseudo, this.password);

    this.authService.login(this.pseudo, this.password).subscribe({
      next: (response) => {
        // Stockage du token
        localStorage.setItem('jwt', response.token);
        console.log('Login réussi, token:', response.token);

        // Redirection après token stocké
        if (this.authService.isLoggedIn()) {
          this.router.navigate(['/home-connected']);
        } else {
          console.error('Token non détecté après login');
        }
      },
      error: (err) => {
        console.error('Erreur de login', err);
        this.errorMessage = err.message;
      }
    });
  }
}
