import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { OuvrageService } from '../../services/ouvrage.service';


@Component({
    selector: 'app-ouvrage-detail-page',
    standalone: true,  // Composant standalone
    imports: [CommonModule],  // Retire DatePipe ici
    templateUrl: './ouvrage-detail-page.html',
    styleUrls: ['./ouvrage-detail-page.scss']
})
export class OuvrageDetailPage implements OnInit {
    ouvrage: any = {};
    loading = true;
    errorMessage = '';

    constructor(
        private route: ActivatedRoute,
        private ouvrageService: OuvrageService
    ) { }

    ngOnInit(): void {
        // Récupérer l'ID de l'ouvrage depuis l'URL
        const ouvrageId = this.route.snapshot.paramMap.get('id');
        console.log('ID de l\'ouvrage :', ouvrageId);  // Vérifie que l'ID est bien récupéré

        if (ouvrageId) {
            this.ouvrageService.getOuvrageById(ouvrageId).subscribe({
                next: (data) => {
                    console.log('Données de l\'ouvrage:', data);
                    this.ouvrage = data;
                    this.loading = false;
                },
                error: (err) => {
                    console.error('Erreur lors de la récupération de l\'ouvrage', err);
                    this.errorMessage = 'Impossible de récupérer les détails de l\'ouvrage';
                    this.loading = false;
                }
            });

        }
    }




    // Utilisation de toLocaleDateString pour formater la date
    formatDate(date: string): string {
        const parsedDate = new Date(date);
        return parsedDate.toLocaleDateString('fr-FR'); // Ou autre locale, selon ton besoin
    }
}

