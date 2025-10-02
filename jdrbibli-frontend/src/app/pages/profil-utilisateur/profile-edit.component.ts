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
    styleUrls: ['./profile-edit.component.scss']
})
export class ProfileEditComponent implements OnInit {
    pseudo = '';
    email = '';
    loading = true;
    message = '';
    error = false;

    constructor(private authService: AuthService) { }

    ngOnInit() {
        this.loadUserInfo();
    }

    // Charge les infos utilisateur depuis le service
    loadUserInfo() {
        this.loading = true;
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
            next: (res: any) => {
                if (res.token) {
                    this.authService.setToken(res.token);
                }

                this.message = res.message || 'Profil mis à jour avec succès.';
                this.error = false;

                this.loadUserInfo();
            },
            error: (err) => {
                console.error('Erreur mise à jour profil:', err);

                // Pop-up générique
                alert('Erreur - veuillez recommencer le processus.');

                // Message visible sur la page
                this.message = err.error?.message
                    ? err.error.message
                    : 'Erreur lors de la mise à jour. Veuillez recommencer.';
                this.error = true;
            },
        });
    }


}
