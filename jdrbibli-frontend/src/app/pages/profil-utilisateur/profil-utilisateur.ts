import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { RouterModule, Router } from '@angular/router';

@Component({
    selector: 'app-profil-utilisateur',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './profil-utilisateur.html',
    styleUrls: ['./profil-utilisateur.scss'] // correction ici (styleUrls, pluriel)
})
export class ProfilUtilisateur implements OnInit {
    pseudo: string = '';
    email: string = '';
    motDePasse: string = '**********'; // mot de passe masqué
    avatarUrl: string = '';

    constructor(private authService: AuthService, private router: Router) { }

    ngOnInit(): void {
        this.loadUserInfo();
    }

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

}
