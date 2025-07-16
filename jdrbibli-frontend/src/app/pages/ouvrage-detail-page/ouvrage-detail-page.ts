import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OuvrageService, OuvrageDTO } from '../../services/ouvrage.service';

@Component({
    selector: 'app-ouvrage-detail-page',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './ouvrage-detail-page.html',
    styleUrls: ['./ouvrage-detail-page.scss']
})
export class OuvrageDetailPage implements OnInit {
    ouvrage: OuvrageDTO | null = null;
    loading = true;
    errorMessage = '';
    ouvragesGamme: OuvrageDTO[] = [];

    constructor(
        private route: ActivatedRoute,
        private ouvrageService: OuvrageService,
        private router: Router
    ) { }

    ngOnInit(): void {
        const ouvrageIdParam = this.route.snapshot.paramMap.get('id');
        const ouvrageId = ouvrageIdParam ? parseInt(ouvrageIdParam, 10) : null;

        if (ouvrageId) {
            this.ouvrageService.getById(ouvrageId).subscribe({
                next: (data) => {
                    console.log('Ouvrage chargé complet:', data);
                    this.ouvrage = data;
                    this.loading = false;

                    const gammeId = data.gammeId;
                    if (gammeId) {
                        console.log('Chargement des autres ouvrages pour gammeId:', gammeId);
                        this.loadOtherOuvragesInGamme(gammeId, data.id!);
                    } else {
                        console.warn('Aucun gammeId trouvé sur cet ouvrage.');
                    }
                },
                error: (err) => {
                    console.error('Erreur lors de la récupération de l\'ouvrage', err);
                    this.errorMessage = 'Impossible de récupérer les détails de l\'ouvrage';
                    this.loading = false;
                }
            });
        } else {
            this.errorMessage = 'ID d\'ouvrage non trouvé dans l\'URL';
            this.loading = false;
        }
    }

    /** ✅ Récupérer les autres ouvrages de la même gamme en excluant celui qu’on consulte */
    loadOtherOuvragesInGamme(gammeId: number, excludeId: number): void {
        this.ouvrageService.getOtherOuvragesInGamme(gammeId, excludeId).subscribe({
            next: (data) => {
                this.ouvragesGamme = data;
                console.log('Autres ouvrages récupérés :', this.ouvragesGamme.map(o => o.titre));
            },
            error: (err) => {
                console.error('Erreur lors du chargement des autres ouvrages de la gamme', err);
            }
        });
    }

    /** Naviguer vers un autre ouvrage */
    ouvrirPageOuvrage(ouvrageId: number): void {
        this.router.navigate(['/ouvrage', ouvrageId]);
    }

    formatDate(date: string): string {
        return new Date(date).toLocaleDateString('fr-FR');
    }
}
