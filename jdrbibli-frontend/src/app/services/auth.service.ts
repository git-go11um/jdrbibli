import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiAuthUrl = 'http://localhost:8084/api/auth';
  private apiUserUrl = 'http://localhost:8084/api/users';
  private currentUser: any = null;

  constructor(private http: HttpClient) { }

  clearAuth(): void {
    localStorage.removeItem('jwt');   // supprime le token
    localStorage.removeItem('user');  // supprime les infos utilisateur
    this.currentUser = null;          // réinitialise la variable interne
  }


  setUserInfo(user: any) {
    this.currentUser = user;
    localStorage.setItem('user', JSON.stringify(user));
  }

  setToken(token: string) {
    localStorage.setItem('jwt', token);  // <-- unifie sur "jwt"
  }

  // ---------------- LOGIN ----------------
  login(pseudo: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiAuthUrl}/login`, { pseudo, password }).pipe(
      tap(res => localStorage.setItem('jwt', res.token)),
      catchError(this.handleError)
    );
  }

  getUserInfo(): Observable<any> {
    const headers = this.authHeaders();
    return this.http.get<any>(`${this.apiUserUrl}/profile/me`, { headers }).pipe(catchError(this.handleError));
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt');
  }

  logout(): void {
    this.clearAuth();
  }

  // ---------------- REGISTER ----------------
  register(pseudo: string, email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiAuthUrl}/register`, { pseudo, email, password })
      .pipe(catchError(this.handleError));
  }

  // ---------------- PASSWORD RESET ----------------
  requestPasswordReset(pseudo: string): Observable<any> {
    return this.http.post<any>(`${this.apiAuthUrl}/password-reset/request`, { pseudo })
      .pipe(catchError(this.handleError));
  }

  verifyResetCode(pseudo: string, code: string): Observable<any> {
    return this.http.post<any>(`${this.apiAuthUrl}/password-reset/verify-code`, { pseudo, code })
      .pipe(catchError(this.handleError));
  }

  confirmReset(pseudo: string, code: string, newPassword: string): Observable<any> {
    return this.http.post<any>(`${this.apiAuthUrl}/password-reset/confirm`, {
      pseudo,
      code,
      newPassword
    }).pipe(catchError(this.handleError));
  }

  // ---------------- PROFILE ----------------
  updateProfile(pseudo: string, email: string, newPassword?: string): Observable<any> {
    const headers = this.authHeaders();
    const body: any = { pseudo, email };
    if (newPassword) body.password = newPassword;

    return this.http.put<any>(`${this.apiAuthUrl}/profile`, body, { headers })
      .pipe(
        tap(res => {
          if (res.token) localStorage.setItem('jwt', res.token);
        }),
        catchError(this.handleError)
      );
  }

  changeProfilePassword(data: { currentPassword: string; newPassword: string; confirmNewPassword: string }): Observable<any> {
    const headers = this.authHeaders();
    return this.http.put<any>(`${this.apiAuthUrl}/profile/password`, data, { headers })
      .pipe(catchError(this.handleError));
  }

  deleteUser(): Observable<any> {
    const userId = this.getUserIdFromToken();
    if (!userId) return throwError(() => new Error('ID utilisateur manquant'));

    const headers = this.authHeaders();
    return this.http.delete<any>(`${this.apiAuthUrl}/${userId}`, { headers })
      .pipe(
        tap(res => console.log('deleteUser response:', res)),
        catchError(this.handleError)
      );
  }


  // ---------------- AVATAR ----------------
  uploadAvatar(fileData: FormData): Observable<string> {
    const headers = this.authHeaders();
    return this.http.put(`${this.apiUserUrl}/profile/avatar`, fileData, {
      headers,
      responseType: 'text'
    }).pipe(catchError(this.handleError));
  }

  // ---------------- JWT UTIL ----------------
  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt');
    return token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : new HttpHeaders();
  }

  public getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt');
    const userId = this.getUserIdFromToken();
    let headers = new HttpHeaders();
    if (token) headers = headers.set('Authorization', `Bearer ${token}`);
    if (userId) headers = headers.set('X-User-Id', userId.toString());
    return headers;
  }


  getUserIdFromToken(): number | null {
    const token = localStorage.getItem('jwt');
    if (!token) {
      console.warn('[AuthService] Aucun token trouvé dans le localStorage.');
      return null;
    }
    try {
      const payload = this.decodeJwt(token);
      console.log('[AuthService] Payload décodé du JWT:', payload);
      return payload?.id ?? null;
    } catch (e) {
      console.error('[AuthService] Erreur décodage JWT pour id:', e);
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

  private handleError(error: HttpErrorResponse) {
    console.error('handleError triggered:', error);
    const msg = error.error instanceof ErrorEvent
      ? `Erreur: ${error.error.message}`
      : `Erreur serveur (${error.status}) - message: ${JSON.stringify(error.error)}`;
    return throwError(() => new Error(msg));
  }

  getToken(): string | null {
    return localStorage.getItem('jwt');
  }
}
