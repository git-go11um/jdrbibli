import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service'; // ajuste le chemin si besoin

@Component({
  selector: 'app-reset-password-newpass-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-password-newpass-page.html',
  styleUrls: ['./reset-password-newpass-page.scss']
})
export class ResetPasswordNewpassPage {
  newPassword: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = "Les mots de passe ne correspondent pas.";
      return;
    }

    const pseudo = localStorage.getItem('resetPseudo');
    const code = localStorage.getItem('resetCode');
    if (!pseudo || !code) {
      this.errorMessage = "Session expirée, recommencez la procédure.";
      this.router.navigate(['/reset-password']);
      return;
    }

    this.authService.resetPassword(pseudo, code, this.newPassword).subscribe({
      next: () => {
        // nettoyage localStorage pour éviter tout souci
        localStorage.removeItem('resetPseudo');
        localStorage.removeItem('resetCode');
        this.router.navigate(['/login']);
      },
      error: () => {
        this.errorMessage = "Erreur lors du changement de mot de passe.";
      }
    });
  }
}
