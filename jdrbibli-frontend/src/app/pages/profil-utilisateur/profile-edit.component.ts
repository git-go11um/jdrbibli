import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-profile-edit',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './profile-edit.component.html',
})
export class ProfileEditComponent implements OnInit {
    pseudo = '';
    email = '';
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

    onSubmit() {
        this.message = '';
        this.error = false;

        this.authService.updateProfile(this.pseudo, this.email).subscribe({
            next: () => {
                this.message = 'Profil mis à jour avec succès.';
                this.error = false;
            },
            error: (err) => {
                console.error('Erreur mise à jour profil:', err);
                this.message = `Erreur lors de la mise à jour: ${err.error?.message || err.message || 'serveur inaccessible'}`;
                this.error = true;
            },
        });
    }
}
