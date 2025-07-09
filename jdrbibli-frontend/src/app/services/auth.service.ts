import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';

  constructor(private http: HttpClient) { }

  login(pseudo: string, password: string) {
    return this.http.post<{ token: string }>(
      `${this.apiUrl}/login`,
      { pseudo, motDePasse: password }
    ).pipe(
      catchError(this.handleError)
    );
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  logout() {
    localStorage.removeItem('token');
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

  private handleError(error: HttpErrorResponse) {
    let msg = 'Erreur inconnue';
    if (error.error instanceof ErrorEvent) {
      msg = `Erreur: ${error.error.message}`;
    } else if (error.status === 401) {
      msg = 'Identifiants invalides';
    } else {
      msg = `Erreur serveur (${error.status})`;
    }
    return throwError(() => new Error(msg));
  }
}
