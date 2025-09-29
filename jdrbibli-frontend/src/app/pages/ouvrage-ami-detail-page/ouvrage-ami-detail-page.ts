import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OuvrageService, OuvrageDTO } from '../../services/ouvrage.service';

@Component({
    selector: 'app-ouvrage-ami-detail-page',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './ouvrage-ami-detail-page.html',
    styleUrls: ['./ouvrage-ami-detail-page.scss']
})
export class OuvrageAmiDetailPage implements OnInit {

    friendId!: number;
    gammeId!: number;
    ouvrage: OuvrageDTO | null = null;
    loading = true;
    errorMessage = '';

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private ouvrageService: OuvrageService
    ) { }

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
            const friendIdParam = params.get('friendId');
            const gammeIdParam = params.get('gammeId');
            const ouvrageIdParam = params.get('ouvrageId');

            this.friendId = friendIdParam ? +friendIdParam : 0;
            this.gammeId = gammeIdParam ? +gammeIdParam : 0;
            const ouvrageId = ouvrageIdParam ? +ouvrageIdParam : 0;

            if (this.friendId && this.gammeId && ouvrageId) {   // <-- ici this.gammeId
                this.loadOuvrage(this.friendId, ouvrageId);
            } else {
                this.errorMessage = 'ID d’ami, de gamme ou d’ouvrage manquant.';
                this.loading = false;
            }
        });
    }




    loadOuvrage(friendId: number, ouvrageId: number): void {
        this.loading = true;
        this.errorMessage = '';
        this.ouvrage = null;

        this.ouvrageService.getFriendOuvrage(friendId, ouvrageId).subscribe({
            next: (data: OuvrageDTO) => {
                this.ouvrage = data;
                this.loading = false;
            },
            error: (err: any) => {
                console.error('Erreur chargement ouvrage ami :', err);
                this.errorMessage = 'Impossible de récupérer les détails de cet ouvrage.';
                this.loading = false;
            }

        });

    }


    /** Retour à la liste des ouvrages de la gamme de l’ami */
    revenirALaGamme() {
        if (this.friendId && this.gammeId) {
            this.router.navigate(['/ouvrage-ami', this.friendId, 'gamme', this.gammeId]);
        } else {
            console.error('Impossible de revenir à la gamme : friendId ou gammeId manquant');
        }
    }








}
