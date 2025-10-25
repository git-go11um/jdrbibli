import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../environments/environment';  // Importer l'environnement

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiAuthUrl = `${environment.apiUrl}/auth`;  // Utilisation dynamique de l'URL
  private apiUserUrl = `${environment.apiUrl}/users`;  // URL utilisateur dynamique
  private currentUser: any = null;

  constructor(private http: HttpClient) { }

  // ---------------- CLEAR AUTH ----------------
  clearAuth(): void {
    localStorage.removeItem('jwt');   // Supprime le token JWT
    localStorage.removeItem('user');  // Supprime les infos utilisateur
    this.currentUser = null;          // Réinitialise l'utilisateur courant
  }

  // ---------------- SET USER & TOKEN ----------------
  setUserInfo(user: any) {
    this.currentUser = user;
    localStorage.setItem('user', JSON.stringify(user));
  }

  setToken(token: string) {
    localStorage.setItem('jwt', token);
  }

  // ---------------- LOGIN ----------------
  login(pseudo: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiAuthUrl}/login`, { pseudo, password }).pipe(
      tap(res => localStorage.setItem('jwt', res.token)),  // Enregistre le JWT dans le localStorage
      catchError(this.handleError)
    );
  }

  // ---------------- GET USER INFO ----------------
  getUserInfo(): Observable<any> {
    const headers = this.authHeaders();
    return this.http.get<any>(`${this.apiUserUrl}/profile/me`, { headers }).pipe(catchError(this.handleError));
  }

  // ---------------- LOGOUT ----------------
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
  updateProfile(pseudo: string, email: string): Observable<any> {
    const headers = this.authHeaders(); // <- pas besoin de X-User-Id ici
    const body = { pseudo, email };

    return this.http.put<any>(`${this.apiAuthUrl}/profile`, body, { headers })
      .pipe(
        tap(res => {
          if (res.token) {
            localStorage.setItem('jwt', res.token);
          }
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
    return this.http.delete<any>(`${this.apiAuthUrl}/profile/${userId}`, { headers })  // Correction : l'ID est dans l'URL de l'API
      .pipe(
        tap(res => {
          console.log('User deleted:', res);
          this.clearAuth();  // Logique supplémentaire pour effacer les informations après suppression
        }),
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

  // Cette méthode est pour obtenir les headers d'authentification avec le token et l'ID utilisateur
  public getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt');
    const userId = this.getUserIdFromToken();
    let headers = new HttpHeaders();
    if (token) headers = headers.set('Authorization', `Bearer ${token}`);
    if (userId) headers = headers.set('X-User-Id', userId.toString());
    return headers;
  }

  // ---------------- UTILITY FUNCTIONS ----------------
  public getUserIdFromToken(): number | null {
    const token = localStorage.getItem('jwt');
    if (!token) {
      console.warn('[AuthService] Aucun token trouvé dans le localStorage.');
      return null;
    }
    try {
      const payload = this.decodeJwt(token);
      return payload?.id ?? null;
    } catch (e) {
      console.error('[AuthService] Erreur décodage JWT pour id:', e);
      return null;
    }
  }

  // Extraction du pseudo de l'utilisateur depuis le JWT
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

  // Décode un JWT et retourne le payload
  private decodeJwt(token: string): any {
    const parts = token.split('.');
    if (parts.length !== 3) throw new Error('Token JWT invalide');
    return JSON.parse(atob(parts[1]));
  }

  // ---------------- ERROR HANDLER ----------------
  private handleError(error: HttpErrorResponse) {
    const msg = error.error instanceof ErrorEvent
      ? `Erreur: ${error.error.message}`
      : `Erreur serveur (${error.status}) - message: ${JSON.stringify(error.error)}`;
    return throwError(() => new Error(msg));
  }

  // Retourne le token JWT actuel
  getToken(): string | null {
    return localStorage.getItem('jwt');
  }

  // ---------------- CHECK IF USER IS LOGGED IN ----------------
  isLoggedIn(): boolean {
    const token = this.getToken();  // Récupère le token JWT du localStorage
    return !!token;  // Retourne true si un token est présent, sinon false
  }
}
