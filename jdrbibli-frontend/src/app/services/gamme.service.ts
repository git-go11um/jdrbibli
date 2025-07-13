import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';  // bien vérifier le chemin

export interface GammeDTO {
    id?: number;
    nom: string;
    description?: string;
}

@Injectable({
    providedIn: 'root'
})
export class GammeService {
    private readonly baseUrl = 'http://localhost:8084/api/ouvrage/gammes';

    constructor(private http: HttpClient, private authService: AuthService) { }

    getAll(): Observable<GammeDTO[]> {
        const headers = this.createHeaders();
        return this.http.get<GammeDTO[]>(this.baseUrl, { headers });
    }

    getById(id: number): Observable<GammeDTO> {
        const headers = this.createHeaders();
        return this.http.get<GammeDTO>(`${this.baseUrl}/${id}`, { headers });
    }

    create(gamme: GammeDTO): Observable<GammeDTO> {
        const headers = this.createHeaders();
        return this.http.post<GammeDTO>(this.baseUrl, gamme, { headers });
    }

    update(id: number, gamme: GammeDTO): Observable<GammeDTO> {
        const headers = this.createHeaders();
        return this.http.put<GammeDTO>(`${this.baseUrl}/${id}`, gamme, { headers });
    }

    delete(id: number, force: boolean = false): Observable<void> {
        const headers = this.createHeaders();
        return this.http.delete<void>(`${this.baseUrl}/${id}?force=${force}`, { headers });
    }

    private createHeaders(): HttpHeaders {
        const pseudo = this.authService.getUserPseudo();
        return new HttpHeaders({
            'X-User-Pseudo': pseudo ?? ''
        });
    }
}
