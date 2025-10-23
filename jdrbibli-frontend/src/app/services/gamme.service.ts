import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface GammeDTO {
    id?: number;
    nom: string;
    description?: string;
}

@Injectable({
    providedIn: 'root'
})
export class GammeService {
    private readonly baseUrl = `${environment.apiUrl}/ouvrage/gammes`;

    constructor(private http: HttpClient) { }

    getAll(): Observable<GammeDTO[]> {
        return this.http.get<GammeDTO[]>(this.baseUrl);
    }

    getById(id: number): Observable<GammeDTO> {
        return this.http.get<GammeDTO>(`${this.baseUrl}/${id}`);
    }

    create(gamme: GammeDTO): Observable<GammeDTO> {
        return this.http.post<GammeDTO>(this.baseUrl, gamme);
    }

    update(id: number, gamme: GammeDTO): Observable<GammeDTO> {
        return this.http.put<GammeDTO>(`${this.baseUrl}/${id}`, gamme);
    }

    delete(id: number, force: boolean = false): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}?force=${force}`);
    }

    /** Récupère les gammes d’un utilisateur ami */
    getFriendGammes(friendId: number): Observable<GammeDTO[]> {
        return this.http.get<GammeDTO[]>(`${this.baseUrl}/friend/${friendId}`);
    }
}
