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
  styleUrl: './login-page.scss'
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
        console.log('Login réussi, token:', response.token);
        // ✅ utilise setLogin pour notifier le header
        this.authService.setLogin(response.token);
        // ✅ navigate ensuite seulement
        this.router.navigate(['/home-connected']);
      },
      error: (err) => {
        console.error('Erreur de login', err);
        this.errorMessage = err.message;
      }
    });
  }
}
