import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-profile-edit',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    template: `
    <div *ngIf="loading">Chargement...</div>

    <form *ngIf="!loading" (ngSubmit)="onSubmit()" #profileForm="ngForm">
      <label for="pseudo">Pseudo :</label><br />
      <input
        id="pseudo"
        name="pseudo"
        [(ngModel)]="pseudo"
        required
        minlength="3"
        maxlength="50"
      /><br />

      <label for="email">Email :</label><br />
      <input
        id="email"
        name="email"
        type="email"
        [(ngModel)]="email"
        required
      /><br />

      <button type="submit" [disabled]="profileForm.invalid">Mettre à jour</button>
    </form>

    <div *ngIf="message" [style.color]="error ? 'red' : 'green'">{{ message }}</div>
  `,
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
            },
            error: (err) => {
                this.message = `Erreur lors de la mise à jour: ${err.message || err}`;
                this.error = true;
            },
        });
    }
}
