import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.scss']
})
export class ProfilePageComponent {
  showDeleteConfirm = false;
  profile: any; // À typer plus tard (ex: UserResponseDTO)

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.userService.getProfile().subscribe({
      next: (data) => {
        this.profile = data;
      },
      error: (err) => {
        console.error('Erreur chargement profil', err);
        alert('Erreur lors du chargement du profil');
      }
    });
  }

  askDelete() {
    this.showDeleteConfirm = true;
  }

  cancelDelete() {
    this.showDeleteConfirm = false;
  }

  confirmDelete() {
    this.userService.deleteAccount().subscribe({
      next: () => {
        // Par exemple rediriger vers la page login ou accueil public
        window.location.href = '/login';
      },
      error: (err) => {
        console.error('Erreur suppression compte', err);
        alert('Erreur lors de la suppression du compte');
      }
    });
  }

  editProfile() {
    // Fonction à implémenter plus tard
  }
}
