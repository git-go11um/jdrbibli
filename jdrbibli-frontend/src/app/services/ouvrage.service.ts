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
    ownerPseudo?: string;
}

@Injectable({
    providedIn: 'root'
})
export class OuvrageService {

    // URL de base exposée par le gateway (comme tu l'as configurée)
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
        return this.http.get<OuvrageDTO>(`http://localhost:8084/api/ouvrage/ouvrages/${id}`, { headers });
    }

    getByGamme(gammeId: number): Observable<OuvrageDTO[]> {
        const headers = this.createHeaders();
        return this.http.get<OuvrageDTO[]>(`http://localhost:8084/api/ouvrage/ouvrages/gammes/${gammeId}`, { headers });
    }

    create(ouvrage: OuvrageDTO): Observable<OuvrageDTO> {
        const headers = this.createHeaders();
        return this.http.post<OuvrageDTO>(this.apiUrl, ouvrage, { headers });
    }

    update(id: number, ouvrage: OuvrageDTO): Observable<OuvrageDTO> {
        const headers = this.createHeaders();
        return this.http.put<OuvrageDTO>(`http://localhost:8084/api/ouvrage/ouvrages/${id}`, ouvrage, { headers });
    }

    delete(id: number): Observable<void> {
        const headers = this.createHeaders();
        return this.http.delete<void>(`http://localhost:8084/api/ouvrage/ouvrages/${id}`, { headers });
    }

    getOuvragesByGammeId(gammeId: number): Observable<OuvrageDTO[]> {
        const headers = this.createHeaders();
        return this.http.get<OuvrageDTO[]>(`http://localhost:8084/api/ouvrage/ouvrages/gammes/${gammeId}`, { headers });
    }

    getOtherOuvragesInGamme(gammeId: number, excludeId: number): Observable<OuvrageDTO[]> {
        const headers = this.createHeaders();
        return this.http.get<OuvrageDTO[]>(`http://localhost:8084/api/ouvrage/ouvrages/gammes/${gammeId}/exclude/${excludeId}`, { headers });
    }



}
