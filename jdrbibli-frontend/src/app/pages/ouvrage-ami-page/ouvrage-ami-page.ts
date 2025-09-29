import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OuvrageService, OuvrageDTO } from '../../services/ouvrage.service';

@Component({
    selector: 'app-ouvrage-ami-page',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './ouvrage-ami-page.html',
    styleUrls: ['./ouvrage-ami-page.scss']
})
export class OuvrageAmiPageComponent implements OnInit {

    friendId: number = 0;
    gammeId: number = 0;
    ouvrages: OuvrageDTO[] = [];
    loading: boolean = true;
    errorMessage: string = '';

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private ouvrageService: OuvrageService

    ) { console.log('OuvrageAmiPageComponent constructeur, friendId =', this.friendId); }

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
            const fId = params.get('friendId');
            const gId = params.get('gammeId');
            this.friendId = fId ? +fId : 0;
            this.gammeId = gId ? +gId : 0;

            if (this.friendId && this.gammeId) {
                this.loadOuvrages();
            } else {
                this.loading = false;
                this.errorMessage = 'ID de l’ami ou de la gamme invalide.';
            }
        });
        console.log('ngOnInit: friendId=', this.friendId, 'gammeId=', this.gammeId);
    }

    loadOuvrages(): void {
        this.loading = true;
        this.errorMessage = '';
        this.ouvrages = [];

        this.ouvrageService.getFriendOuvrages(this.friendId, this.gammeId).subscribe({
            next: (data: OuvrageDTO[]) => {
                this.ouvrages = data || [];
                this.loading = false;
            },
            error: (err) => {
                console.error('Erreur chargement ouvrages ami:', err);
                this.errorMessage = 'Impossible de charger les ouvrages de cette gamme.';
                this.loading = false;
            }
        });

    }

    ouvrirDetail(ouvrageId: number): void {
        this.router.navigate([
            '/ouvrage-ami',
            this.friendId,
            'gamme',
            this.gammeId,
            'ouvrage',
            ouvrageId
        ]);
    }

/** Retour à la ludothèque de l’ami */
retourLudothequeAmi(): void {
    this.router.navigate(['/ludotheque-ami', this.friendId]);
  }
  

}
