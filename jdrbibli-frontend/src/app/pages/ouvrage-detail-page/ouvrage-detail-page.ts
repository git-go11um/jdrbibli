import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OuvrageService, OuvrageDTO } from '../../services/ouvrage.service';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-ouvrage-detail-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './ouvrage-detail-page.html',
  styleUrls: ['./ouvrage-detail-page.scss']
})
export class OuvrageDetailPage implements OnInit {
  ouvrage: OuvrageDTO | null = null;
  imageSrc: string | null = null;
  loading = true;
  errorMessage = '';
  ouvragesGamme: OuvrageDTO[] = [];

  constructor(
    private route: ActivatedRoute,
    private ouvrageService: OuvrageService,
    private router: Router,
    private http: HttpClient,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const ouvrageIdParam = params.get('id');
      const ouvrageId = ouvrageIdParam ? parseInt(ouvrageIdParam, 10) : null;

      if (!ouvrageId) {
        this.errorMessage = 'ID d’ouvrage non trouvé dans l’URL';
        this.loading = false;
        return;
      }

      this.loading = true;

      this.ouvrageService.getById(ouvrageId).subscribe({
        next: (data) => {
          console.log('📘 Ouvrage chargé complet:', data);
          this.ouvrage = data;
          this.loading = false;

          // ✅ Chargement sécurisé de l'image via JWT
          if (data.imageUrl) {
            let imageUrl = data.imageUrl.trim();

            // ✅ Cas 1 : l’URL contient déjà /api/uploads (éviter doublon)
            if (imageUrl.startsWith('/api/uploads/') || imageUrl.startsWith('api/uploads/')) {
              // On retire le /api de trop si le baseApi le contient déjà
              const baseApi = environment.apiUrl.replace(/\/api\/?$/, '');
              imageUrl = imageUrl.replace(/^\/?api\//, 'api/');
              var imageEndpoint = `${baseApi}/${imageUrl}`;

              // ✅ Cas 2 : l’URL commence directement par /uploads/
            } else if (imageUrl.startsWith('/uploads/') || imageUrl.startsWith('uploads/')) {
              var imageEndpoint = `${environment.apiUrl}/${imageUrl.replace(/^\/?/, '')}`;

              // ✅ Cas 3 : l’URL est absolue (déjà complète)
            } else if (imageUrl.startsWith('http')) {
              var imageEndpoint = imageUrl;

              // ✅ Cas par défaut
            } else {
              var imageEndpoint = `${environment.apiUrl}/uploads/${imageUrl}`;
            }

            console.log('🖼️ Endpoint image final:', imageEndpoint);

            this.http.get(imageEndpoint, {
              headers: this.authService.getAuthHeaders(),
              responseType: 'blob'
            }).subscribe({
              next: blob => {
                this.imageSrc = URL.createObjectURL(blob);
                console.log('✅ Blob image ouvrage chargé:', blob);
              },
              error: err => console.error('❌ Erreur chargement image ouvrage :', err)
            });
          }




          // 🔁 Charger les autres ouvrages de la même gamme
          if (data.gammeId) {
            console.log('📚 Chargement des autres ouvrages pour gammeId:', data.gammeId);
            this.loadOtherOuvragesInGamme(data.gammeId, data.id!);
          }
        },
        error: (err) => {
          console.error('Erreur lors de la récupération de l’ouvrage', err);
          this.errorMessage = 'Impossible de récupérer les détails de l’ouvrage';
          this.loading = false;
        }
      });
    });
  }

  /** 🔁 Récupérer les autres ouvrages de la même gamme (en excluant celui consulté) */
  loadOtherOuvragesInGamme(gammeId: number, excludeId: number): void {
    this.ouvrageService.getOtherOuvragesInGamme(gammeId, excludeId).subscribe({
      next: (data) => {
        this.ouvragesGamme = data;
        console.log('📚 Autres ouvrages récupérés :', this.ouvragesGamme.map(o => o.titre));
      },
      error: (err) => {
        console.error('Erreur lors du chargement des autres ouvrages de la gamme', err);
      }
    });
  }

  ouvrirPageOuvrage(id: number): void {
    this.router.navigate(['/ouvrage-detail', id]);
  }

  ouvrirPageGamme(gammeId: number): void {
    this.router.navigate(['/gamme', gammeId]);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR');
  }
}
