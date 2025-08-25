import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-reset-password-newpass-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-password-newpass-page.html',
  styleUrls: ['./reset-password-newpass-page.scss']
})
export class ResetPasswordNewpassPage {
  pseudo: string;   // pseudo de l’utilisateur
  code: string;
  newPassword: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {
    this.pseudo = localStorage.getItem('resetPseudo') || '';
    this.code = localStorage.getItem('resetCode') || '';
  }

  onSubmit(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = "Les mots de passe ne correspondent pas.";
      return;
    }

    if (!this.code || !this.pseudo) {
      this.errorMessage = "Session expirée, recommencez la procédure.";
      this.router.navigate(['/reset-password']);
      return;
    }

    console.log('pseudo:', this.pseudo);
    console.log('code:', this.code);
    console.log('newPassword:', this.newPassword);

    this.authService.confirmReset(this.pseudo, this.code, this.newPassword).subscribe({
      next: () => {
        localStorage.removeItem('resetCode');
        localStorage.removeItem('resetPseudo');
        this.router.navigate(['/login']);
      },
      error: (err: HttpErrorResponse) => {
        console.error('Erreur lors de la réinitialisation du mot de passe :', err);
        this.errorMessage = err?.error?.message || "Erreur lors de la réinitialisation du mot de passe.";
      }
    });
  }
}  
