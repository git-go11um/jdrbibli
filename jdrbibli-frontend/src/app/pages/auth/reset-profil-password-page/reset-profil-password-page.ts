import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';  // Assure-toi d'importer FormsModule
import { AuthService } from '../../../services/auth.service'; // Ajuste le chemin si nécessaire

@Component({
  selector: 'app-reset-profil-password-page',
  standalone: true,
  imports: [CommonModule, FormsModule],  // Ajout de FormsModule dans les imports
  templateUrl: './reset-profil-password-page.html',
  styleUrls: ['./reset-profil-password-page.scss']
})
export class ResetProfilPasswordPage {
  newPassword: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService) { }

  onSubmit() {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = "Les mots de passe ne correspondent pas.";
      return;
    }

    const email = localStorage.getItem('userEmail');  // On récupère l'email de l'utilisateur dans localStorage
    if (!email) {
      this.errorMessage = "Session expirée, recommencez la procédure.";
      return;
    }

    this.authService.changeProfilePassword(email, this.newPassword).subscribe({
      next: () => {
        localStorage.removeItem('userEmail'); // Nettoyage du localStorage
        alert("Mot de passe modifié avec succès !");
      },
      error: (err) => {
        this.errorMessage = `Erreur lors du changement de mot de passe : ${err.message}`;
      }
    });
  }
}
