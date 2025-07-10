import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
    private apiUrl = 'http://localhost:8084/auth';

    constructor(private http: HttpClient) { }

    deleteAccount(): Observable<any> {
        return this.http.delete(`${this.apiUrl}/delete`);
    }

    getProfile(): Observable<any> {
        return this.http.get(`${this.apiUrl}/me`);
    }
}
