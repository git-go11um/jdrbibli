import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';

interface UserProfile {
  pseudo: string;
  email: string;
  avatarUrl?: string;
}

@Component({
  selector: 'app-profile-page',
  standalone: true,
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.scss'
})
export class ProfilePageComponent implements OnInit {
  userProfile?: UserProfile;
  selectedFile: File | null = null;

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.userService.getUserProfile().subscribe({
      next: (profile) => {
        this.userProfile = profile;
      },
      error: (err) => {
        console.error('Erreur chargement profil', err);
      }
    });
  }

  onEdit() {
    this.router.navigate(['/edit-profile']);
  }

  onDelete() {
    if (confirm("Êtes-vous sûr de vouloir supprimer votre compte ?")) {
      this.userService.deleteAccount().subscribe({
        next: () => {
          alert('Votre compte a été supprimé avec succès.');
          // Rediriger vers la page d'accueil ou de déconnexion après la suppression
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Erreur lors de la suppression du compte', err);
          alert('Une erreur est survenue lors de la suppression de votre compte.');
        }
      });
    }
  }

  onAvatarSubmit(event: Event): void {
    event.preventDefault(); // Empêche le rechargement de la page

    if (this.selectedFile) {
      // Créer un objet FormData pour envoyer le fichier
      const formData = new FormData();
      formData.append('image', this.selectedFile, this.selectedFile.name);

      // Appel à un service pour uploader l'avatar
      this.userService.uploadAvatar(formData).subscribe({
        next: (response) => {
          // Mettre à jour l'URL de l'avatar dans le profil après un upload réussi
          this.userProfile!.avatarUrl = response.avatarUrl; // Assurer que l'avatarUrl est dans la réponse
        },
        error: (err) => {
          console.error('Erreur lors de l\'upload de l\'avatar', err);
        }
      });
    } else {
      console.log('Aucun fichier sélectionné');
    }
  }


  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      // Créer un objet FormData
      const formData = new FormData();
      formData.append('image', file, file.name); // Ajout du fichier au FormData

      // Appel à l'API pour uploader l'avatar avec FormData
      this.userService.uploadAvatar(formData).subscribe({
        next: (response) => {
          console.log('Image téléchargée avec succès', response);
          // Une fois l'image téléchargée, on peut mettre à jour l'URL de l'image dans le profil
          this.userProfile!.avatarUrl = response.imageUrl; // Assurez-vous que l'API renvoie le lien vers l'image
        },
        error: (err) => {
          console.error('Erreur lors du téléchargement de l\'image', err);
        }
      });
    }
  }




}
