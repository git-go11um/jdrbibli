import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { GammeDTO } from '../models/gamme.model';
import { environment } from '../environments/environment';

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

    private apiUrl = `${environment.apiUrl}/ouvrages`;

    constructor(private http: HttpClient, private authService: AuthService) { }

    // ---------------- CRUD classique ----------------
    getAll(): Observable<OuvrageDTO[]> {
        return this.http.get<OuvrageDTO[]>(this.apiUrl);
    }

    getById(id: number): Observable<OuvrageDTO> {
        return this.http.get<OuvrageDTO>(`${this.apiUrl}/${id}`);
    }

    getByGammeId(gammeId: number): Observable<OuvrageDTO[]> {
        return this.http.get<OuvrageDTO[]>(`${this.apiUrl}/by-gamme/${gammeId}`);
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

    getOtherOuvragesInGamme(gammeId: number, excludeId: number): Observable<OuvrageDTO[]> {
        return this.http.get<OuvrageDTO[]>(`${this.apiUrl}/gammes/${gammeId}/exclude/${excludeId}`);
    }

    // ---------------- Upload d'image ----------------
    uploadImage(file: File) {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post(`${environment.apiUrl}/ouvrages/upload-image`, formData, { responseType: 'text' });
    }

    // ---------------- Ludothèque d’un ami ----------------
    getFriendLibrary(friendId: number): Observable<OuvrageDTO[]> {
        const headers = this.authService.getAuthHeaders();
        return this.http.get<OuvrageDTO[]>(`${this.apiUrl}/friends/${friendId}/ouvrages`, { headers });
    }

    getFriendOuvrages(friendId: number, gammeId: number): Observable<OuvrageDTO[]> {
        const headers = this.authService.getAuthHeaders();
        return this.http.get<OuvrageDTO[]>(`${this.apiUrl}/friend/${friendId}/gamme/${gammeId}`, { headers });
    }

    getFriendOuvrage(friendId: number, ouvrageId: number): Observable<OuvrageDTO> {
        const headers = this.authService.getAuthHeaders();
        return this.http.get<OuvrageDTO>(`${this.apiUrl}/friend/${friendId}/${ouvrageId}`, { headers });
    }


    getFriendGammes(friendId: number): Observable<GammeDTO[]> {
        const headers = this.authService.getAuthHeaders();
        return this.http.get<GammeDTO[]>(`${this.apiUrl}/gammes/friend/${friendId}`, { headers });
    }

    getFriendOuvrageById(friendId: number, ouvrageId: number) {
        const headers = this.authService.getAuthHeaders();
        return this.http.get<OuvrageDTO>(`${this.apiUrl}/friend/${friendId}/${ouvrageId}`, { headers });
    }

    getOuvrageImage(imageUrl: string): Observable<Blob> {
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        });

        // imageUrl correspond à "/uploads/images/xxx.jpg"
        return this.http.get(`http://localhost:8084/api${imageUrl}`, {
            headers,
            responseType: 'blob'
        });
    }

}
