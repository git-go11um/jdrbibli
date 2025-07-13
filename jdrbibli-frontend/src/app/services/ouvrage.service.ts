import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface OuvrageDTO {
    id?: number;
    titre: string;
    description: string;
    version: string;
    typeOuvrage: string;
    datePublication: string;
    langue: string;
    editeur: string;
    etat: string;
    isbn: string;
    ouvrageLie: string;
    scenarioLie: string;
    pret: boolean;
    errata: string;
    notes: string;
    scenariosContenus: string[];
    autresOuvragesGamme: string[];
    gammeId: number;  // L'ID de la gamme à laquelle appartient l'ouvrage
    liensMedias: [];
}

@Injectable({
    providedIn: 'root'
})
export class OuvrageService {

    private apiUrl = 'http://localhost:8084/api/ouvrages';  // L'URL de ton API backend pour les ouvrages

    constructor(private http: HttpClient, private authService: AuthService) { }


    // Récupérer tous les ouvrages
    getAll(): Observable<OuvrageDTO[]> {
        const headers = this.createHeaders();
        return this.http.get<OuvrageDTO[]>(this.apiUrl, { headers });
    }


    // Récupérer un ouvrage par son ID
    getById(id: number): Observable<OuvrageDTO> {
        return this.http.get<OuvrageDTO>(`${this.apiUrl}/${id}`);
    }

    // Créer un nouvel ouvrage
    create(ouvrage: OuvrageDTO): Observable<OuvrageDTO> {
        return this.http.post<OuvrageDTO>(this.apiUrl, ouvrage);
    }

    // Mettre à jour un ouvrage existant
    update(id: number, ouvrage: OuvrageDTO): Observable<OuvrageDTO> {
        return this.http.put<OuvrageDTO>(`${this.apiUrl}/${id}`, ouvrage);
    }

    // Supprimer un ouvrage
    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    getByGamme(gammeId: number): Observable<OuvrageDTO[]> {
        return this.http.get<OuvrageDTO[]>(`${this.apiUrl}/gammes/${gammeId}/ouvrages`); // L'URL doit correspondre à ton API
    }

    private createHeaders(): HttpHeaders {
        const pseudo = this.authService.getUserPseudo();
        return new HttpHeaders({
            'X-User-Pseudo': pseudo ?? ''
        });
    }


}
