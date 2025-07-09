import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';   // Ajouté ici
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service'; // ajuste le chemin si besoin

@Component({
  selector: 'app-reset-password-newpass-page',
  standalone: true,
  imports: [CommonModule, FormsModule],  // CommonModule ajouté ici
  templateUrl: './reset-password-newpass-page.html',
  styleUrls: ['./reset-password-newpass-page.scss']
})
export class ResetPasswordNewpassPage {
  newPassword: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    const pseudo = localStorage.getItem('resetPseudo');
    const code = localStorage.getItem('resetCode');

    if (!pseudo || !code) {
      this.errorMessage = 'Session expirée, recommencez la procédure.';
      this.router.navigate(['/reset-password']);
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas.';
      return;
    }

    this.authService.changePassword(pseudo, this.newPassword, code).subscribe({
      next: () => {
        // Nettoyer le localStorage
        localStorage.removeItem('resetPseudo');
        localStorage.removeItem('resetCode');
        // Rediriger vers login
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.errorMessage = err.message;
      }
    });
  }
}
