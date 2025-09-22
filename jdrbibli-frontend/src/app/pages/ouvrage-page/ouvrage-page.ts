import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { OuvrageService, OuvrageDTO } from '../../services/ouvrage.service';

@Component({
  selector: 'app-ouvrage-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ouvrage-page.html',
  styleUrls: ['./ouvrage-page.scss']
})
export class OuvragePage implements OnInit {

  ouvrage: OuvrageDTO | undefined;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ouvrageService: OuvrageService
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : undefined;

    if (id) {
      this.loadOuvrage(id);
    } else {
      console.error('ID d\'ouvrage invalide ou manquant');
    }
  }

  loadOuvrage(id: number): void {
    this.loading = true;
    this.ouvrageService.getById(id).subscribe({
      next: (data) => {
        this.ouvrage = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement ouvrage :', err);
        this.loading = false;
      }
    });
  }

  editerOuvrage(): void {
    if (this.ouvrage?.id) {
      this.router.navigate(['/creation-edition', this.ouvrage.id]);
    } else {
      console.error('Ouvrage non trouvé ou ID manquant');
    }
  }
}
