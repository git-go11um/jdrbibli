import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-reset-profil-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <h2>Modifier mon mot de passe</h2>
    <form [formGroup]="form" (ngSubmit)="onSubmit()">
      <div>
        <label for="currentPassword">Mot de passe actuel :</label>
        <input type="password" id="currentPassword" formControlName="currentPassword" required />
      </div>

      <div>
        <label for="newPassword">Nouveau mot de passe :</label>
        <input type="password" id="newPassword" formControlName="newPassword" required />
      </div>

      <div>
        <label for="confirmNewPassword">Confirmer le nouveau mot de passe :</label>
        <input type="password" id="confirmNewPassword" formControlName="confirmNewPassword" required />
      </div>

      <button type="submit" [disabled]="form.invalid">Valider</button>
    </form>

    <p *ngIf="successMessage" style="color: green">{{ successMessage }}</p>
    <p *ngIf="errorMessage" style="color: red">{{ errorMessage }}</p>
  `
})
export class ResetProfilPasswordComponent implements OnInit {
  form: FormGroup;
  successMessage = '';
  errorMessage = '';

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.form = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmNewPassword: ['', Validators.required],
    });
  }

  ngOnInit() {
    console.log('ResetProfilPasswordComponent initialisé');
  }

  onSubmit() {
    if (this.form.invalid) return;

    const { currentPassword, newPassword, confirmNewPassword } = this.form.value;

    if (newPassword !== confirmNewPassword) {
      this.errorMessage = 'Les deux nouveaux mots de passe ne correspondent pas.';
      this.successMessage = '';
      return;
    }

    const token = localStorage.getItem('jwt');
    if (!token) {
      this.errorMessage = 'Utilisateur non authentifié.';
      this.successMessage = '';
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const payload = {
      currentPassword,
      newPassword
    };

    this.http.put<any>('http://localhost:8084/auth/profile/password', payload, { headers })
      .subscribe({
        next: (res) => {
          this.successMessage = res.message || 'Mot de passe mis à jour avec succès.';
          this.errorMessage = '';

          if (res.data) {
            localStorage.setItem('jwt', res.data); // ✅ Nouveau token JWT reçu et stocké
          }

          this.form.reset(); // ✅ Réinitialise le formulaire après succès
        },
        error: (err) => {
          console.error('Erreur complète reçue du backend:', err);

          if (err.status === 403) {
            this.errorMessage = 'Accès interdit. Veuillez vérifier vos informations.';
          } else if (err.status === 401) {
            this.errorMessage = 'Session expirée. Veuillez vous reconnecter.';
          } else {
            this.errorMessage = err.error?.message || 'Erreur lors de la mise à jour du mot de passe.';
          }

          this.successMessage = '';
        }
      });
  }
}
