import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';  // Import du service AuthService
import { RouterModule, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

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
    selectedFile: File | null = null;

    constructor(private authService: AuthService, private router: Router, private http: HttpClient) { }

    ngOnInit(): void {
        this.loadUserInfo();  // Charger les infos de l'utilisateur au démarrage
        this.testUserService();
    }
    testUserService(): void {
        this.http.get('/api/users').subscribe(
            (response) => {
                console.log('User service is working:', response);
            },
            (error) => {
                console.error('User service error:', error);
            }
        );
    }

    // Charger les infos utilisateur depuis l'API
    loadUserInfo(): void {
        this.authService.getUserInfo().subscribe(
            (data) => {
                this.pseudo = data.pseudo;
                this.email = data.email;
                // Correction ici :
                this.avatarUrl = data.avatarUrl || ''
                    ? (data.avatarUrl.startsWith('http') ? data.avatarUrl : 'http://localhost:4200' + data.avatarUrl)
                    : '';
            },
            (error) => {
                console.error('Erreur lors du chargement des informations utilisateur', error);
            }
        );
    }

    onFileSelected(event: any): void {
        const file = event.target.files[0];
        if (file) {
            // Vérifier le type de fichier
            if (!file.type.startsWith('image/')) {
                alert('Veuillez sélectionner une image');
                return;
            }

            // Vérifier la taille (max 5MB)
            if (file.size > 5 * 1024 * 1024) {
                alert('L\'image est trop volumineuse. Taille maximum : 5MB');
                return;
            }

            this.selectedFile = file;
        }
    }

    uploadAvatar(): void {
        if (!this.selectedFile) {
            alert('Veuillez sélectionner une image');
            return;
        }

        const formData = new FormData();
        formData.append('file', this.selectedFile);

        const token = localStorage.getItem('jwt');
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

        this.http.post(`/api/users/${this.pseudo}/avatar`, formData, { headers })
            .subscribe(
                (response: any) => {
                    this.avatarUrl = response.avatarUrl;
                    this.selectedFile = null;
                    alert('Avatar mis à jour avec succès !');
                },
                (error) => {
                    console.error('Erreur lors de l\'upload de l\'avatar', error);
                    alert('Erreur lors de l\'upload de l\'avatar');
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
