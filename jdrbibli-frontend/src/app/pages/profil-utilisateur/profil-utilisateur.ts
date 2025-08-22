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
    motDePasse: string = '**********'; // mot de passe masqué
    avatarUrl: string = '';
    selectedFile: File | null = null;

    constructor(public authService: AuthService, private router: Router) { }

    ngOnInit(): void {
        this.loadUserInfo();
    }

    /** Charge les informations de l’utilisateur */
    loadUserInfo(): void {
        this.authService.getUserInfo().subscribe({
            next: (data) => {
                this.pseudo = data.pseudo;
                this.email = data.email;
                this.avatarUrl = data.avatarUrl || '';
            },
            error: (error) => {
                console.error('Erreur lors du chargement des informations utilisateur', error);
            }
        });
    }

    /** Supprime le compte utilisateur */
    deleteUser(): void {
        if (confirm('Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.')) {
            const userPseudo = this.authService.getUserPseudo();
            if (!userPseudo) {
                console.error('Pseudo utilisateur manquant');
                return;
            }

            this.authService.deleteUser(userPseudo).subscribe({
                next: (res) => {
                    console.log('Suppression réussie, réponse:', res);
                    this.authService.logout();
                    this.router.navigate(['/home-public']);
                },
                error: (err) => {
                    console.error('Erreur lors de la suppression du compte:', err);
                    alert('Une erreur est survenue lors de la suppression de votre compte.');
                }
            });
        }
    }

    /** Sélection d’un fichier pour l’avatar */
    onFileSelected(event: Event): void {
        const fileInput = event.target as HTMLInputElement;
        if (fileInput.files && fileInput.files.length > 0) {
            this.selectedFile = fileInput.files[0];
        }
    }

    /** Upload de l’avatar */
    uploadAvatar(): void {
        if (!this.selectedFile) {
            alert('Veuillez sélectionner un fichier avant d’uploader.');
            return;
        }

        const formData = new FormData();
        formData.append('file', this.selectedFile);

        // On réinitialise selectedFile pour éviter toute interférence
        const fileToUpload = this.selectedFile;
        this.selectedFile = null;

        // Appel au service, qui doit inclure le JWT dans l'header
        this.authService.uploadAvatar(formData).subscribe({
            next: (res: any) => {
                console.log('Avatar uploadé avec succès', res);
                this.loadUserInfo(); // recharger les infos pour afficher le nouvel avatar
                alert('Avatar mis à jour avec succès !');
            },
            error: (err) => {
                console.error('Erreur lors de l’upload de l’avatar', err);
                alert('Une erreur est survenue lors de l’upload de l’avatar. Vérifie le token ou le backend.');
            }
        });
    }



    /** Redirige vers la page de réinitialisation du mot de passe */
    goToResetPassword(): void {
        this.router.navigate(['/reset-profil-password']);
    }
}
