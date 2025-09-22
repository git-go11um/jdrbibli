import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-profil-utilisateur',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './profil-utilisateur.html',
  styleUrls: ['./profil-utilisateur.scss']
})
export class ProfilUtilisateur implements OnInit {
  pseudo: string = '';
  email: string = '';
  motDePasse: string = '**********';
  avatarUrl: string | null = null;
  selectedFile: File | null = null;

  constructor(public authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.loadUserInfo();
  }

  loadUserInfo(): void {
    this.authService.getUserInfo().subscribe({
      next: (data) => {
        console.log('Avatar URL reçu du backend:', data.avatarUrl);
        this.pseudo = data.pseudo;
        this.email = data.email;
        this.avatarUrl = data.avatarUrl
          ? `http://localhost:8084${data.avatarUrl}?t=${new Date().getTime()}`
          : null;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des informations utilisateur', error);
      }
    });
  }

  deleteUser(): void {
    if (!confirm('Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.')) return;

    this.authService.deleteUser().subscribe({  // <-- utilise maintenant l’ID JWT
      next: () => {
        console.log('Compte supprimé');
        this.authService.logout();
        this.router.navigate(['/home-public']);
      },
      error: (err: any) => {
        console.error('Erreur lors de la suppression du compte:', err);
        alert('Une erreur est survenue lors de la suppression du compte.');
      }
    });
  }

  onFileSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedFile = fileInput.files[0];
    }
  }

  uploadAvatar(): void {
    if (!this.selectedFile) {
      alert('Veuillez sélectionner un fichier avant d’uploader.');
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);
    this.selectedFile = null;

    this.authService.uploadAvatar(formData).subscribe({
      next: () => this.loadUserInfo(),
      error: (err: any) => console.error('Erreur upload avatar:', err)
    });
  }

  goToResetPassword(): void {
    this.router.navigate(['/reset-profil-password']);
  }
}
