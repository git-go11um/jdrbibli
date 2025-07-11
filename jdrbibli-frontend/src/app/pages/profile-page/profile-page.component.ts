import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

interface UserProfile {
  pseudo: string;
  email: string;
  avatarUrl?: string;
}

@Component({
  selector: 'app-profile-page',
  standalone: true,
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.scss'],
  imports: [FormsModule, CommonModule],
})
export class ProfilePageComponent implements OnInit {
  userProfile: UserProfile | null = null;
  selectedFile: File | null = null;
  isEditing: boolean = false;
  defaultAvatarUrl = 'assets/default-avatar.png'; // Image par défaut

  editedUserProfile: UserProfile = {
    pseudo: '',
    email: '',
    avatarUrl: ''
  };

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    this.userService.getUserProfile().subscribe((profile) => {
      this.userProfile = profile;
      if (profile) {
        this.editedUserProfile = { ...profile };
      }
    });
  }

  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      const formData = new FormData();
      formData.append('image', file, file.name);

      this.userService.uploadAvatar(formData).subscribe({
        next: (response) => {
          console.log('Image téléchargée avec succès', response);
          this.userProfile!.avatarUrl = response.imageUrl;
        },
        error: (err) => {
          console.error('Erreur lors du téléchargement de l\'image', err);
        }
      });
    }
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (!this.isEditing) {
      this.editedUserProfile = { ...this.userProfile! };
    }
  }

  saveProfile(): void {
    this.userService.updateUserProfile(this.editedUserProfile).subscribe((updatedProfile) => {
      this.userProfile = updatedProfile;
      this.isEditing = false;
    });
  }

  onDelete(): void {
    console.log('Suppression du compte');
  }
}
