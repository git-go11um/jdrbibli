import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8084/api/auth';

  constructor(private http: HttpClient) { }

  // --- LOGIN ---
  login(pseudo: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(
      `${this.apiUrl}/login`,
      { pseudo, password }
    ).pipe(
      tap(response => {
        localStorage.setItem('jwt', response.token);
        console.log('Login réussi, token:', response.token);
      }),
      catchError(this.handleError)
    );
  }

  // --- USER INFO ---
  getUserInfo(): Observable<any> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any>(`${this.apiUrl}/me`, { headers })
      .pipe(catchError(this.handleError));
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt');
  }

  logout(): void {
    localStorage.removeItem('jwt');
  }

  // --- REGISTER ---
  register(pseudo: string, email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, { pseudo, email, password })
      .pipe(catchError(this.handleError));
  }

  // --- PASSWORD RESET ---
  requestPasswordReset(pseudo: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/password-reset/request`, { pseudo })
      .pipe(catchError(this.handleError));
  }

  verifyResetCode(pseudo: string, code: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/password-reset/verify-code`, { pseudo, code })
      .pipe(catchError(this.handleError));
  }

  changePassword(pseudo: string, newPassword: string, code: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/password-reset/change`, { pseudo, newPassword, code })
      .pipe(catchError(this.handleError));
  }

  validateResetCode(pseudo: string, code: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/validate-reset-code`, { pseudo, code })
      .pipe(catchError(this.handleError));
  }

  resetPassword(pseudo: string, code: string, newPassword: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/reset-password`, { pseudo, code, newPassword })
      .pipe(catchError(this.handleError));
  }

  // --- PROFILE ---
  updateProfile(pseudo: string, email: string, newPassword?: string): Observable<any> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const body: any = { pseudo, email };
    if (newPassword) body.password = newPassword;
    return this.http.put<any>(`${this.apiUrl}/profile`, body, { headers })
      .pipe(catchError(this.handleError));
  }

  changeProfilePassword(data: { currentPassword: string; newPassword: string; confirmNewPassword: string }): Observable<any> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<any>(`${this.apiUrl}/profile/password`, data, { headers })
      .pipe(catchError(this.handleError));
  }

  deleteUser(pseudo: string): Observable<any> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<any>(`${this.apiUrl}/${pseudo}`, { headers })
      .pipe(
        tap(response => console.log('Réponse backend deleteUser:', response)),
        catchError(this.handleError)
      );
  }

  // --- JWT UTIL ---
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
    const decoded = atob(parts[1]);
    return JSON.parse(decoded);
  }

  // --- AVATAR ---
  uploadAvatar(fileData: FormData): Observable<any> {
    const token = localStorage.getItem('jwt');
    if (!token) return throwError(() => new Error('JWT manquant'));
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<any>('http://localhost:8084/user/profile/avatar', fileData, { headers })
      .pipe(catchError(this.handleError));
  }

  // --- ERROR HANDLER ---
  private handleError(error: HttpErrorResponse) {
    console.error('handleError triggered avec:', error);
    let msg = 'Erreur inconnue';
    if (error.error instanceof ErrorEvent) {
      msg = `Erreur: ${error.error.message}`;
    } else {
      msg = `Erreur serveur (${error.status}) - message: ${JSON.stringify(error.error)}`;
    }
    return throwError(() => new Error(msg));
  }
}
