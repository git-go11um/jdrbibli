import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { GammeService, GammeDTO } from '../../services/gamme.service';

@Component({
    selector: 'app-gamme-ami-page',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './gamme-ami-page.html',
    styleUrls: ['./gamme-ami-page.scss']
})
export class GammeAmiPageComponent implements OnInit {

    friendId: number = 0;
    gammes: GammeDTO[] = [];
    loading: boolean = true;
    errorMessage: string = '';

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private gammeService: GammeService
    ) { }

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
            const id = params.get('friendId');
            this.friendId = id ? +id : 0;
            console.log('[DEBUG] friendId récupéré:', this.friendId);

            if (this.friendId) {
                this.loadFriendGammes();
            } else {
                this.loading = false;
                this.errorMessage = 'ID de l’ami invalide.';
            }
        });
    }

    loadFriendGammes(): void {
        this.loading = true;
        this.errorMessage = '';
        this.gammeService.getFriendGammes(this.friendId).subscribe({
            next: (data: GammeDTO[]) => {
                this.gammes = data || [];
                this.loading = false;
            },
            error: (err) => {
                console.error('[DEBUG] Erreur HTTP getFriendGammes:', err);
                this.errorMessage = 'Impossible de charger les gammes de cet ami.';
                this.loading = false;
            }
        });
    }

    ouvrirGamme(gammeId: number) {
        this.router.navigate(['/gamme-ami', this.friendId, gammeId]);
    }

}
