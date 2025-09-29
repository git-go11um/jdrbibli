import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service'; // Ajuste le chemin si nécessaire

@Component({
  selector: 'app-reset-profil-password-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-profil-password-page.html',
  styleUrls: ['./reset-profil-password-page.scss']
})
export class ResetProfilPasswordPage {
  newPassword: string = '';
  confirmNewPassword: string = '';
  currentPassword: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService) { }

  onSubmit() {
    this.errorMessage = '';

    console.log('currentPassword:', this.currentPassword);
    console.log('newPassword:', this.newPassword);
    console.log('confirmNewPassword:', this.confirmNewPassword);

    if (this.newPassword !== this.confirmNewPassword) {
      this.errorMessage = "Les mots de passe ne correspondent pas.";
      return;
    }

    const payload = {
      currentPassword: this.currentPassword,
      newPassword: this.newPassword,
      confirmNewPassword: this.confirmNewPassword
    };

    console.log("Payload envoyé au backend :", payload);

    this.authService.changeProfilePassword(payload).subscribe({
      next: () => {
        localStorage.removeItem('userPseudo');
        alert("Mot de passe modifié avec succès !");
      },
      error: (err) => {
        console.error('Erreur mise à jour mot de passe:', err);

        // Pop-up générique
        alert('Erreur - veuillez recommencer le processus.');

        // Message visible sur la page
        this.errorMessage = err.error?.message
          ? err.error.message
          : 'Erreur lors de la mise à jour du mot de passe. Veuillez recommencer.';
      }
    });
  }



  ngOnInit() {
    console.log('reset-profil-password-page.ts utilisé');
  }
}
