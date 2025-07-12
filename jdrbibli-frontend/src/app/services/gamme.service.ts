import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/** Interface représentant une gamme côté frontend */
export interface GammeDTO {
    id?: number;
    nom: string;
    description?: string;
}

@Injectable({
    providedIn: 'root'
})
export class GammeService {
    private readonly baseUrl = 'http://localhost:8084/api/ouvrage/gammes'; // adapte le port si besoin

    constructor(private http: HttpClient) { }

    /** Récupère toutes les gammes */
    getAll(): Observable<GammeDTO[]> {
        return this.http.get<GammeDTO[]>(this.baseUrl);
    }

    /** Récupère une gamme par son ID */
    getById(id: number): Observable<GammeDTO> {
        return this.http.get<GammeDTO>(`${this.baseUrl}/${id}`);
    }

    /** Crée une nouvelle gamme */
    create(gamme: GammeDTO): Observable<GammeDTO> {
        return this.http.post<GammeDTO>(this.baseUrl, gamme);
    }

    /** Met à jour une gamme existante */
    update(id: number, gamme: GammeDTO): Observable<GammeDTO> {
        return this.http.put<GammeDTO>(`${this.baseUrl}/${id}`, gamme);
    }

    /** Supprime une gamme (avec ou sans force) */
    delete(id: number, force: boolean = false): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}?force=${force}`);
    }
}
