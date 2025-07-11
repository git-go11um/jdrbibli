import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';  // Import du service AuthService
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-profil-utilisateur',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './profil-utilisateur.html',
    styleUrl: './profil-utilisateur.scss'
})
export class ProfilUtilisateur implements OnInit {
    pseudo: string = '';
    email: string = '';
    motDePasse: string = '**********';  // Le mot de passe restera masqué
    avatarUrl: string = '';

    constructor(private authService: AuthService) { }

    ngOnInit(): void {
        this.loadUserInfo();  // Appeler la méthode pour charger les infos de l'utilisateur lors du chargement du composant
    }

    // Méthode pour charger les informations de l'utilisateur
    loadUserInfo(): void {
        this.authService.getUserInfo().subscribe(
            (data) => {
                this.pseudo = data.pseudo;
                this.email = data.email;
                // L'avatar est optionnel, donc on vérifie s'il existe
                this.avatarUrl = data.avatarUrl || '';  // Si pas d'avatar, on laisse une chaîne vide
            },
            (error) => {
                console.error('Erreur lors du chargement des informations utilisateur', error);
            }
        );
    }

    // Méthode pour supprimer le compte
    deleteUser(): void {
        const confirmDeletion = confirm('Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.');

        if (confirmDeletion) {
            const userId = this.getUserPseudoFromLocalStorage();  // Récupérer l'ID de l'utilisateur

            if (!userId) {
                alert('ID utilisateur manquant');
                return;
            }

            this.authService.deleteUser(userId).subscribe(
                () => {
                    alert('Votre compte a été supprimé avec succès.');
                    this.authService.logout();  // Déconnexion après suppression
                    window.location.href = '/';  // Redirection vers la page d'accueil
                },
                (error) => {
                    alert('Une erreur est survenue lors de la suppression de votre compte.');
                    console.error(error);
                }
            );
        }
    }

    // Méthode pour récupérer le pseudo de l'utilisateur à partir du stockage local
    getUserPseudoFromLocalStorage(): string | null {
        const token = localStorage.getItem('jwt');
        if (token) {
            const decodedToken = this.authService.decodeJwt(token);  // Utilisation de la méthode decodeJwt dans le service
            return decodedToken.sub;  // Le pseudo est dans le champ "sub"
        }
        return null;
    }








}
