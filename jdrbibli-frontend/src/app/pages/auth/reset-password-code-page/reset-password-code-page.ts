import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service'; // ajuste le chemin si besoin

@Component({
  selector: 'app-reset-password-code-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-password-code-page.html',
  styleUrls: ['./reset-password-code-page.scss']
})
export class ResetPasswordCodePage {
  code: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    const pseudo = localStorage.getItem('resetPseudo');
    if (!pseudo) {
      this.errorMessage = 'Session expirée, recommencez la procédure.';
      this.router.navigate(['/reset-password']);
      return;
    }

    this.authService.validateResetCode(pseudo, this.code).subscribe({
      next: () => {
        // Enregistrer le code en local pour la suite
        localStorage.setItem('resetCode', this.code);
        // Rediriger vers la page pour entrer le nouveau mdp
        this.router.navigate(['/reset-password-newpass']);
      },
      error: (err) => {
        this.errorMessage = err.message || 'Code invalide.';
      }
    });
  }
}
