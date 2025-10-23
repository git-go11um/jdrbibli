import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-avatar-test',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2>Test Upload Avatar</h2>
    <input type="file" (change)="onFileSelected($event)" accept="image/*">
    <button (click)="uploadAvatar()" [disabled]="!selectedFile">Envoyer Avatar</button>
    <p *ngIf="message">{{ message }}</p>
  `
})
export class AvatarTestComponent {
  selectedFile: File | null = null;
  message = '';

  constructor(private http: HttpClient) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.message = `Fichier sélectionné : ${this.selectedFile.name}`;
    }
  }

  uploadAvatar() {
    if (!this.selectedFile) {
      this.message = 'Sélectionnez un fichier !';
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    const token = localStorage.getItem('jwt');
    if (!token) {
      this.message = 'JWT manquant';
      return;
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const url = `${environment.apiUrl}/user/profile/avatar`;

    this.http.put(url, formData, { headers })
      .subscribe({
        next: (res) => {
          this.message = 'Upload réussi !';
          console.log('Réponse backend:', res);
        },
        error: (err) => {
          this.message = 'Erreur lors de l’upload. Vérifie JWT et CORS.';
          console.error('Erreur upload avatar', err);
        }
      });
  }
}
