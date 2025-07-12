import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';  // Import du service AuthService
import { RouterModule, Router } from '@angular/router';

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

    constructor(private authService: AuthService, private router: Router) { }

    ngOnInit(): void {
        this.loadUserInfo();  // Charger les infos de l'utilisateur au démarrage
    }

    // Charger les infos utilisateur depuis l'API
    loadUserInfo(): void {
        this.authService.getUserInfo().subscribe(
            (data) => {
                this.pseudo = data.pseudo;
                this.email = data.email;
                this.avatarUrl = data.avatarUrl || '';
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
            const userPseudo = this.getUserPseudoFromLocalStorage();  // Récupérer le pseudo

            if (!userPseudo) {
                alert('Pseudo utilisateur manquant');
                return;
            }

            this.authService.deleteUser(userPseudo).subscribe(
                () => {
                    alert("Merci d'avoir utilisé nos services, n'hésitez pas à revenir.");
                    this.authService.logout();  // Déconnexion après suppression
                    this.router.navigate(['/home-public']);  // Redirection vers la page publique d'accueil
                },
                (error) => {
                    alert('Une erreur est survenue lors de la suppression de votre compte.');
                    console.error(error);
                }
            );
        }
    }



    // Récupérer le pseudo depuis le token JWT local
    getUserPseudoFromLocalStorage(): string | null {
        const token = localStorage.getItem('jwt');
        if (token) {
            const decodedToken = this.authService.decodeJwt(token);
            return decodedToken.sub;  // Le pseudo est dans le champ "sub"
        }
        return null;
    }
}
