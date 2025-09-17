import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

    private apiUrl = 'http://localhost:8084/api/ouvrage/ouvrages';

    constructor(private http: HttpClient) { }

    getAll(): Observable<OuvrageDTO[]> {
        return this.http.get<OuvrageDTO[]>(this.apiUrl);
    }

    getById(id: number): Observable<OuvrageDTO> {
        return this.http.get<OuvrageDTO>(`${this.apiUrl}/${id}`);
    }

    getByGamme(gammeId: number): Observable<OuvrageDTO[]> {
        return this.http.get<OuvrageDTO[]>(`${this.apiUrl}/gammes/${gammeId}`);
    }

    create(ouvrage: OuvrageDTO): Observable<OuvrageDTO> {
        return this.http.post<OuvrageDTO>(this.apiUrl, ouvrage);
    }

    update(id: number, ouvrage: OuvrageDTO): Observable<OuvrageDTO> {
        return this.http.put<OuvrageDTO>(`${this.apiUrl}/${id}`, ouvrage);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    getOuvragesByGammeId(gammeId: number): Observable<OuvrageDTO[]> {
        return this.http.get<OuvrageDTO[]>(`${this.apiUrl}/gammes/${gammeId}`);
    }

    getOtherOuvragesInGamme(gammeId: number, excludeId: number): Observable<OuvrageDTO[]> {
        return this.http.get<OuvrageDTO[]>(`${this.apiUrl}/gammes/${gammeId}/exclude/${excludeId}`);
    }
}
