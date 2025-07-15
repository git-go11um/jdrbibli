import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8084/api/auth';

  constructor(private http: HttpClient) { }

  login(pseudo: string, password: string) {
    return this.http.post<{ token: string }>(
      `${this.apiUrl}/login`,
      { pseudo, motDePasse: password }
    ).pipe(
      tap((response: { token: string }) => {
        localStorage.setItem('jwt', response.token);
        console.log('Login réussi, token:', response.token);
      }),
      catchError(this.handleError)
    );
  }

  getUserInfo() {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<any>(`${this.apiUrl}/me`, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt');
  }

  logout() {
    localStorage.removeItem('jwt');
  }

  register(pseudo: string, email: string, password: string) {
    console.log("Envoi de l'inscription : ", { pseudo, email, motDePasse: password });
    return this.http.post<any>(
      `${this.apiUrl}/register`,
      { pseudo, email, motDePasse: password }
    ).pipe(
      catchError(this.handleError)
    );
  }

  requestPasswordReset(pseudo: string) {
    console.log('Demande de réinitialisation envoyée pour :', pseudo);
    return this.http.post<any>(
      `${this.apiUrl}/password-reset/request`,
      { pseudo }
    ).pipe(
      catchError(this.handleError)
    );
  }

  verifyResetCode(pseudo: string, code: string) {
    return this.http.post<any>(
      `${this.apiUrl}/password-reset/verify-code`,
      { pseudo, code }
    ).pipe(
      catchError(this.handleError)
    );
  }

  changePassword(pseudo: string, newPassword: string, code: string) {
    return this.http.post<any>(
      `${this.apiUrl}/password-reset/change`,
      { pseudo, newPassword, code }
    ).pipe(
      catchError(this.handleError)
    );
  }

  changeProfilePassword(email: string, newPassword: string) {
    const url = `/auth/profile/password`;
    return this.http.put(url, { email, newPassword });
  }

  deleteUser(pseudo: string): Observable<any> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.delete(`${this.apiUrl}/${pseudo}`, { headers }).pipe(
      tap(response => {
        console.log('Réponse backend deleteUser:', response);
      }),
      catchError(this.handleError)
    );
  }

  updateProfile(pseudo: string, email: string, newPassword?: string) {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const url = this.apiUrl + '/profile';

    const body: any = { pseudo, email };
    if (newPassword) {
      body.password = newPassword;
    }

    return this.http.put(url, body, { headers });
  }

  getUserIdFromToken(): number | null {
    const token = localStorage.getItem('jwt');
    if (!token) return null;

    const payload = this.decodeJwt(token);
    return payload ? payload.id : null;
  }

  getUserPseudo(): string | null {
    const token = localStorage.getItem('jwt');
    if (!token) return null;

    try {
      const payload = this.decodeJwt(token);
      return payload?.sub || null;
    } catch (e) {
      console.error('Erreur décodage JWT pour pseudo:', e);
      return null;
    }
  }

  decodeJwt(token: string): any {
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('Token JWT invalide');
    }
    const decoded = atob(parts[1]);
    return JSON.parse(decoded);
  }

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

  validateResetCode(pseudo: string, code: string) {
    return this.http.post(`${this.apiUrl}/validate-reset-code`, { pseudo, code });
  }

  resetPassword(pseudo: string, code: string, newPassword: string) {
    return this.http.post(`${this.apiUrl}/reset-password`, { pseudo, code, newPassword });
  }
}
