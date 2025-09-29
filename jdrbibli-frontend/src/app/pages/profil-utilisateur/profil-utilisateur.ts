import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { FriendService } from '../../services/friend.service';
import { RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-profil-utilisateur',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './profil-utilisateur.html',
  styleUrls: ['./profil-utilisateur.scss']
})
export class ProfilUtilisateur implements OnInit {
  pseudo: string = '';
  email: string = '';
  motDePasse: string = '**********';
  avatarUrl: string | null = null;
  selectedFile: File | null = null;

  searchPseudo: string = '';
  searchedUser: any = null;
  searchedUserAvatarUrl: string = '';
  requestSent: boolean = false;
  friends: any[] = [];
  friendsAvatarUrls: { [key: number]: string } = {};
  receivedRequests: any[] = [];

  constructor(
    public authService: AuthService,
    private friendService: FriendService,
    private router: Router,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadFriends();
    this.loadReceivedRequests();
  }

  loadUserInfo(): void {
    this.authService.getUserInfo().subscribe({
      next: (data: any) => {
        this.pseudo = data.pseudo;
        this.email = data.email;
        this.avatarUrl = data.avatarUrl
          ? `http://localhost:8084${data.avatarUrl}?t=${new Date().getTime()}`
          : null;
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des informations utilisateur', error);
      }
    });
  }

  deleteUser(): void {
    if (!confirm('Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.')) return;

    this.authService.deleteUser().subscribe({
      next: () => {
        this.authService.logout();
        this.router.navigate(['/home-public']);
      },
      error: (err: any) => alert('Une erreur est survenue lors de la suppression du compte.')
    });
  }

  onFileSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) this.selectedFile = fileInput.files[0];
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

  // ---------------- AMIS ----------------
  loadFriends(): void {
    const userId = this.authService.getUserIdFromToken();
    if (!userId) return;
    this.friendService.listFriends(userId).subscribe({
      next: (data: any) => {
        this.friends = data;
        // Génération des URL avatars amis
        this.friends.forEach(friend => {
          this.friendsAvatarUrls[friend.id] = friend.avatarUrl
            ? `http://localhost:8084${friend.avatarUrl}?t=${new Date().getTime()}`
            : '';
        });
      },
      error: (err: any) => console.error('Erreur chargement amis', err)
    });
  }

  loadReceivedRequests(): void {
    const userId = this.authService.getUserIdFromToken();
    if (!userId) return;
    this.friendService.listReceivedRequests(userId).subscribe({
      next: (data: any) => this.receivedRequests = data,
      error: (err: any) => console.error('Erreur chargement demandes', err)
    });
  }

  acceptFriend(requestId: number): void {
    this.friendService.acceptRequest(requestId).subscribe({
      next: () => {
        this.loadFriends();
        this.loadReceivedRequests();
      },
      error: (err: any) => console.error(err)
    });
  }

  rejectFriend(requestId: number): void {
    this.friendService.rejectRequest(requestId).subscribe({
      next: () => this.loadReceivedRequests(),
      error: (err: any) => console.error(err)
    });
  }

  removeFriend(friendId: number): void {
    const userId = this.authService.getUserIdFromToken();
    if (!userId) return;
    this.friendService.removeFriend(userId, friendId).subscribe({
      next: () => this.loadFriends(),
      error: (err: any) => console.error(err)
    });
  }

  // ---------------- RECHERCHE ET DEMANDE D'AMI ----------------
  searchFriend(): void {
    if (!this.searchPseudo) return;

    if (this.searchPseudo === this.pseudo) {
      alert('Vous ne pouvez vous ajouter en tant qu’ami sur ce site, mais soyez ami avec vous-même s’il vous plaît ;P');
      return;
    }

    this.http.get<any>(
      `http://localhost:8084/api/users/search?pseudo=${this.searchPseudo}`,
      { headers: this.authService.getAuthHeaders() }
    ).subscribe({
      next: (user: any) => {
        this.searchedUser = user;
        this.searchedUserAvatarUrl = user.avatarUrl
          ? `http://localhost:8084${user.avatarUrl}?t=${new Date().getTime()}`
          : '';
        this.requestSent = false;
      },
      error: (err: any) => {
        console.error('Utilisateur non trouvé', err);
        this.searchedUser = null;
        alert('Pseudo inconnu');
      }
    });
  }

  sendFriendRequest(userId: number | null | undefined): void {
    const senderId = this.authService.getUserIdFromToken();
    if (!senderId || !userId) return;

    this.friendService.sendRequest(senderId, userId).subscribe({
      next: () => {
        this.requestSent = true;
        this.searchedUser = null;
      },
      error: (err: any) => console.error('Erreur envoi demande d\'ami', err)
    });
  }

  getFriendAvatarUrl(friend: any): string {
    return this.friendsAvatarUrls[friend.id] || '';
  }
}
