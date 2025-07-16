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
    gammeId: number;
    liensMedias: string[];

}

@Injectable({
    providedIn: 'root'
})
export class OuvrageService {

    private apiUrl = 'http://localhost:8084/api/ouvrage/ouvrages';

    constructor(private http: HttpClient, private authService: AuthService) { }

    private createHeaders(): HttpHeaders {
        const pseudo = this.authService.getUserPseudo() ?? '';
        return new HttpHeaders({ 'X-User-Pseudo': pseudo });
    }

    getAll(): Observable<OuvrageDTO[]> {
        const headers = this.createHeaders();
        return this.http.get<OuvrageDTO[]>(this.apiUrl, { headers });
    }

    getById(id: number): Observable<OuvrageDTO> {
        const headers = this.createHeaders();
        return this.http.get<OuvrageDTO>(`${this.apiUrl}/${id}`, { headers });
    }

    /** Nouvelle méthode propre et claire */
    getByGamme(gammeId: number): Observable<OuvrageDTO[]> {
        const headers = this.createHeaders();
        return this.http.get<OuvrageDTO[]>(`${this.apiUrl}/gamme/${gammeId}`, { headers });
    }

    create(ouvrage: OuvrageDTO): Observable<OuvrageDTO> {
        const headers = this.createHeaders();
        return this.http.post<OuvrageDTO>(this.apiUrl, ouvrage, { headers });
    }

    update(id: number, ouvrage: OuvrageDTO): Observable<OuvrageDTO> {
        const headers = this.createHeaders();
        return this.http.put<OuvrageDTO>(`${this.apiUrl}/${id}`, ouvrage, { headers });
    }

    delete(id: number): Observable<void> {
        const headers = this.createHeaders();
        return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
    }

    getOuvrageById(id: string): Observable<any> {
        const token = localStorage.getItem('jwt');  // Récupère le token depuis le localStorage
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);  // Ajoute le token dans les en-têtes

        // Si apiUrl est déjà "http://localhost:8084/api/ouvrage/", tu n'as pas besoin d'ajouter /ouvrages/
        const url = `${this.apiUrl}${id}`;  // Juste concaténer l'ID sans "/ouvrages"

        return this.http.get(url, { headers });  // Envoie la requête GET avec le token dans l'en-tête
    }




}
