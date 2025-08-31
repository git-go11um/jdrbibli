// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8084/api/auth'; // Gateway URL
  private currentUser: any = null;

  setUserInfo(user: any) {
    this.currentUser = user;
    // Si tu utilises localStorage pour persister l’utilisateur
    localStorage.setItem('user', JSON.stringify(user));
  }

  setToken(token: string) {
    localStorage.setItem('token', token);
  }

  constructor(private http: HttpClient) { }

  // ---------------- LOGIN ----------------
  login(pseudo: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, { pseudo, password }).pipe(
      tap(res => localStorage.setItem('jwt', res.token)),
      catchError(this.handleError)
    );
  }

  getUserInfo(): Observable<any> {
    const headers = this.authHeaders();
    return this.http.get<any>(`${this.apiUrl}/me`, { headers }).pipe(catchError(this.handleError));
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt');
  }

  logout(): void {
    localStorage.removeItem('jwt');
  }

  // ---------------- REGISTER ----------------
  register(pseudo: string, email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, { pseudo, email, password })
      .pipe(catchError(this.handleError));
  }

  // ---------------- PASSWORD RESET ----------------
  requestPasswordReset(pseudo: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/password-reset/request`, { pseudo })
      .pipe(catchError(this.handleError));
  }

  verifyResetCode(pseudo: string, code: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/password-reset/verify-code`, { pseudo, code })
      .pipe(catchError(this.handleError));
  }

  confirmReset(pseudo: string, code: string, newPassword: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/password-reset/confirm`, {
      pseudo: pseudo,
      code: code,               // <-- corrige ici
      newPassword: newPassword
    }).pipe(catchError(this.handleError));
  }


  // ---------------- PROFILE ----------------
  updateProfile(pseudo: string, email: string, newPassword?: string): Observable<any> {
    const headers = this.authHeaders();
    const body: any = { pseudo, email };
    if (newPassword) body.password = newPassword;

    return this.http.put<any>(`${this.apiUrl}/profile`, body, { headers })
      .pipe(
        tap(res => {
          if (res.token) {
            localStorage.setItem('jwt', res.token); // met à jour le token
          }
        }),
        catchError(this.handleError)
      );
  }


  changeProfilePassword(data: { currentPassword: string; newPassword: string; confirmNewPassword: string }): Observable<any> {
    const headers = this.authHeaders();
    return this.http.put<any>(`${this.apiUrl}/profile/password`, data, { headers })
      .pipe(catchError(this.handleError));
  }

  deleteUser(pseudo: string): Observable<any> {
    const headers = this.authHeaders();
    return this.http.delete<any>(`${this.apiUrl}/${pseudo}`, { headers })
      .pipe(
        tap(res => console.log('deleteUser response:', res)),
        catchError(this.handleError)
      );
  }

  // ---------------- AVATAR ----------------
  uploadAvatar(fileData: FormData): Observable<any> {
    const headers = this.authHeaders();
    return this.http.put<any>('http://localhost:8084/user/profile/avatar', fileData, { headers })
      .pipe(catchError(this.handleError));
  }

  // ---------------- JWT UTIL ----------------
  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt');
    return token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : new HttpHeaders();
  }

  getUserIdFromToken(): number | null {
    const token = localStorage.getItem('jwt');
    if (!token) return null;
    try {
      const payload = this.decodeJwt(token);
      return payload?.id ?? null;
    } catch (e) {
      console.error('Erreur décodage JWT pour id:', e);
      return null;
    }
  }

  getUserPseudo(): string | null {
    const token = localStorage.getItem('jwt');
    if (!token) return null;
    try {
      const payload = this.decodeJwt(token);
      return payload?.sub ?? null;
    } catch (e) {
      console.error('Erreur décodage JWT pour pseudo:', e);
      return null;
    }
  }

  private decodeJwt(token: string): any {
    const parts = token.split('.');
    if (parts.length !== 3) throw new Error('Token JWT invalide');
    return JSON.parse(atob(parts[1]));
  }

  // ---------------- ERROR HANDLER ----------------
  private handleError(error: HttpErrorResponse) {
    console.error('handleError triggered:', error);
    const msg = error.error instanceof ErrorEvent
      ? `Erreur: ${error.error.message}`
      : `Erreur serveur (${error.status}) - message: ${JSON.stringify(error.error)}`;
    return throwError(() => new Error(msg));
  }
}
