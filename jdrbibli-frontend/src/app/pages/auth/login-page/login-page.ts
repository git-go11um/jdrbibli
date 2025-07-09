import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NgIf } from '@angular/common'; // <-- ajoute ceci

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [FormsModule, RouterLink, NgIf],  // <-- ajoute NgIf ici
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
        localStorage.setItem('token', response.token);
        this.router.navigate(['/success-login']);
      },
      error: (err) => {
        console.error('Erreur de login', err);
        this.errorMessage = err.message;
      }
    });
  }

}
