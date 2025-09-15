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

    private extractUserIdFromToken(token: string): number | null {
        const decodedToken = this.decodeToken(token);  // Fonction pour décoder le JWT
        return decodedToken ? decodedToken.id : null;  // Retourner l'ID si présent
    }

    // Fonction pour décoder le JWT
    private decodeToken(token: string): any {
        try {
            const payload = token.split('.')[1];  // Extraire la partie payload du JWT
            const decoded = atob(payload);  // Décoder en Base64
            return JSON.parse(decoded);  // Convertir en objet JSON
        } catch (e) {
            console.error("Erreur de décodage du token", e);
            return null;
        }
    }

    private createHeaders(): HttpHeaders {
        const token = this.authService.getToken();  // Récupérer le token

        if (!token) {
            // Si le token est null, on peut soit lancer une erreur, soit retourner des headers vides
            console.error("Token is null");
            return new HttpHeaders(); // Retourner des headers vides si pas de token
        }

        const userId = this.extractUserIdFromToken(token);  // Extraire l'ID de l'utilisateur

        let headers = new HttpHeaders();

        // Ajouter l'ID de l'utilisateur dans les headers si l'ID est valide
        if (userId !== null) {
            headers = headers.set('X-User-Id', userId.toString());
        }

        // Ajouter le token dans les headers
        headers = headers.set('Authorization', `Bearer ${token}`);

        return headers;
    }






}
