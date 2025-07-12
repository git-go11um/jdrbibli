import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-profile-edit',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './profile-edit.component.html', // On a extrait le HTML
})
export class ProfileEditComponent implements OnInit {
    pseudo = '';
    email = '';
    newPassword = '';            // Nouveau mot de passe
    confirmNewPassword = '';     // Confirmation du mot de passe
    passwordMismatch = false;    // Validation de correspondance des mots de passe
    loading = true;
    message = '';
    error = false;

    constructor(private authService: AuthService) { }

    ngOnInit() {
        this.authService.getUserInfo().subscribe({
            next: (user) => {
                this.pseudo = user.pseudo;
                this.email = user.email;
                this.loading = false;
            },
            error: (err) => {
                this.message = 'Erreur lors du chargement du profil.';
                this.error = true;
                this.loading = false;
            },
        });
    }

    // Vérifie que les mots de passe correspondent
    checkPasswordMatch() {
        this.passwordMismatch = this.newPassword !== this.confirmNewPassword;
    }

    onSubmit() {
        this.message = '';
        this.error = false;

        // Vérifier si les mots de passe correspondent
        if (this.newPassword && this.newPassword !== this.confirmNewPassword) {
            this.passwordMismatch = true;
            return; // On ne soumet pas si les mots de passe ne correspondent pas
        }

        this.authService.updateProfile(this.pseudo, this.email, this.newPassword).subscribe({
            next: () => {
                this.message = 'Profil mis à jour avec succès.';
            },
            error: (err) => {
                this.message = `Erreur lors de la mise à jour: ${err.message || err}`;
                this.error = true;
            },
        });
    }

    



}
