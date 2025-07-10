import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
    pseudo: string;
    email: string;
    imageUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
    private apiUrl = 'http://localhost:8084/api/users';  // Via gateway

    constructor(private http: HttpClient) { }

    // Récupérer profil
    getUserProfile(): Observable<UserProfile> {
        return this.http.get<UserProfile>(`${this.apiUrl}/me`);
    }

    // Supprimer compte
    deleteAccount(): Observable<any> {
        return this.http.delete(`${this.apiUrl}/me`);
    }

    // (Plus tard) Modifier profil
    updateProfile(profileData: Partial<UserProfile>): Observable<UserProfile> {
        return this.http.put<UserProfile>(`${this.apiUrl}/me`, profileData);
    }

    uploadAvatar(formData: FormData): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/upload-avatar`, formData);
    }
}
