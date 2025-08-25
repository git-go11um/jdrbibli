import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service'; // ajuste le chemin si besoin
import { HttpErrorResponse } from '@angular/common/http';

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

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit(): void {
    const pseudo = localStorage.getItem('resetPseudo');
    if (!pseudo) {
      this.errorMessage = 'Session expirée, recommencez la procédure.';
      this.router.navigate(['/reset-password']);
      return;
    }

    if (this.code.length !== 6) {
      this.errorMessage = 'Le code doit comporter 6 caractères.';
      return;
    }


    // 🔹 FORCER LE CODE EN MAJUSCULES
    this.code = this.code.toUpperCase();

    this.authService.verifyResetCode(pseudo, this.code).subscribe({
      next: (res: any) => {
        console.log('Réponse serveur:', res.message);
        localStorage.setItem('resetCode', this.code);
        this.router.navigate(['/reset-password-newpass']);
      },
      error: (err: any) => {
        console.error('Erreur lors de la vérification du code :', err);
        this.errorMessage = err?.error?.message || "Erreur inconnue";
      }
    });
  }


}
