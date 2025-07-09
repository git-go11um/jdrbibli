import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-reset-password-request',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './reset-password-request.component.html',
  styleUrls: ['./reset-password-request.component.scss']
})
export class ResetPasswordRequestComponent {
  pseudo = '';
  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit(form: NgForm) {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid) {
      this.errorMessage = 'Veuillez saisir votre pseudo.';
      return;
    }

    this.loading = true;

    this.authService.requestPasswordReset(this.pseudo).subscribe({
      next: () => {
        this.successMessage = 'Si un compte existe, un email a été envoyé.';
        localStorage.setItem('resetPseudo', this.pseudo); // optionnel si besoin
        this.router.navigate(['/reset-password-code']); // redirection après succès
        this.errorMessage = '';
      },
      error: (err: any) => {
        console.error('Erreur demande réinitialisation :', err);
        if (err.status === 0) {
          this.errorMessage = "Serveur indisponible, réessayez plus tard.";
        } else if (err.error?.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Erreur lors de la demande.';
        }
        this.successMessage = '';
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}
