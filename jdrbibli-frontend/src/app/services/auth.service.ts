import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, catchError, throwError, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8084/api/auth';

  // État observable de la connexion (initialisé selon la présence du token)
  private loggedIn = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$: Observable<boolean> = this.loggedIn.asObservable();

  constructor(private http: HttpClient) { }

  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }

  login(pseudo: string, password: string) {
    return this.http.post<{ token: string }>(
      `${this.apiUrl}/login`,
      { pseudo, motDePasse: password }
    ).pipe(
      catchError(this.handleError)
    );
  }

  // À appeler **après** un login réussi pour stocker le token et notifier l'état connecté
  setLogin(token: string) {
    localStorage.setItem('token', token);
    this.loggedIn.next(true);
  }

  // Vérification synchrone (utile dans certains cas)
  isLoggedIn(): boolean {
    return this.loggedIn.value;
  }

  logout() {
    localStorage.removeItem('token');
    this.loggedIn.next(false);
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

  validateResetCode(pseudo: string, code: string) {
    return this.http.post(`${this.apiUrl}/validate-reset-code`, { pseudo, code });
  }

  resetPassword(pseudo: string, code: string, newPassword: string) {
    return this.http.post(`${this.apiUrl}/reset-password`, { pseudo, code, newPassword });
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
