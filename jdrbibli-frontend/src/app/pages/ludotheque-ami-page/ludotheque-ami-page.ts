import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GammeService, GammeDTO } from '../../services/gamme.service';

@Component({
    selector: 'app-ludotheque-ami-page',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './ludotheque-ami-page.html',
    styleUrls: ['./ludotheque-ami-page.scss']
})
export class LudothequeAmiPageComponent implements OnInit {

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
        this.gammes = [];

        this.gammeService.getFriendGammes(this.friendId).subscribe({
            next: (data: GammeDTO[]) => {
                if (!data || data.length === 0) {
                    this.gammes = [];
                    this.errorMessage = 'Cet ami n’a aucune gamme.';
                } else {
                    this.gammes = data;
                }
                this.loading = false;
            },
            error: (err) => {
                console.error('Erreur chargement gammes ami:', err);
                this.errorMessage = 'Impossible de récupérer les gammes de cet ami.';
                this.loading = false;
            }
        });
    }

    ouvrirPageGamme(gammeId: number): void {
        this.router.navigate(['/ouvrage-ami', this.friendId, 'gamme', gammeId]);
    }


}
