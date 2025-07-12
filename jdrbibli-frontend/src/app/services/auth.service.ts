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
        // Stocke le token dans localStorage
        localStorage.setItem('jwt', response.token);
        console.log('Login réussi, token:', response.token);
      }),
      catchError(this.handleError)
    );
  }

  // Nouvelle méthode pour récupérer les informations de l'utilisateur connecté
  getUserInfo() {
    const token = localStorage.getItem('jwt'); // Récupère le token
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`); // Ajoute le token dans les en-têtes

    return this.http.get<any>(`${this.apiUrl}/me`, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt');  // Vérifie la présence du token
  }

  logout() {
    localStorage.removeItem('jwt'); // Supprime le token lors de la déconnexion
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
    const url = `/auth/profile/password`; // URL de l'API backend pour changer le mot de passe
    return this.http.put(url, { email, newPassword });
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

  validateResetCode(pseudo: string, code: string) {
    return this.http.post(`${this.apiUrl}/validate-reset-code`, { pseudo, code });
  }

  resetPassword(pseudo: string, code: string, newPassword: string) {
    return this.http.post(`${this.apiUrl}/reset-password`, { pseudo, code, newPassword });
  }

  deleteUser(pseudo: string): Observable<any> {
    const token = localStorage.getItem('jwt'); // Récupère le token JWT
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`); // Ajoute le token dans l'en-tête

    return this.http.delete(`${this.apiUrl}/${pseudo}`, { headers }).pipe(
      catchError(this.handleError)
    );


  }


  // Cette méthode extrait l'ID de l'utilisateur depuis le token JWT
  getUserIdFromToken(): number | null {
    const token = localStorage.getItem('jwt');
    if (!token) return null;

    // Décoder le token pour en extraire l'ID utilisateur
    const payload = this.decodeJwt(token);
    return payload ? payload.id : null; // Récupérer l'ID de l'utilisateur dans le JWT
  }

  // Décoder le JWT sans librairie externe (ta méthode)
  decodeJwt(token: string): any {
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('Token JWT invalide');
    }
    const decoded = atob(parts[1]);  // Décoder la partie centrale du JWT
    return JSON.parse(decoded);  // Retourne le contenu JSON du token
  }

  updateProfile(pseudo: string, email: string, newPassword?: string) {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const url = this.apiUrl + '/profile';

    // Construire l'objet à envoyer, y compris le mot de passe si fourni
    const body: any = {
      pseudo,
      email,
    };

    // Si un mot de passe est fourni, on l'ajoute à l'objet body
    if (newPassword) {
      body.password = newPassword;
    }

    // Effectuer la requête PUT avec les données et les headers
    return this.http.put(url, body, { headers });
  }







}
