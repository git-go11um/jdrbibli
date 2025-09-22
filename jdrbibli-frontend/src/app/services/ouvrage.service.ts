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
    gammeId?: number | null;
    ownerPseudo?: string;
    ownerId?: number;
    imageUrl?: string;
    gammeNom?: string;
}


@Injectable({
    providedIn: 'root'
})
export class OuvrageService {

    private apiUrl = 'http://localhost:8084/api/ouvrage/ouvrages'; // ton API principale

    constructor(private http: HttpClient) { }

    // CRUD classique
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

    // Upload d'image
    uploadImage(file: File) {
        const formData = new FormData();
        formData.append('file', file);

        return this.http.post(
            'http://localhost:8083/api/ouvrage/ouvrages/upload-image',
            formData,
            { responseType: 'text' }  // ← important !
        );
    }
}

