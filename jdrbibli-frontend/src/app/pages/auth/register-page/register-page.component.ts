import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss']
})
export class RegisterPage {
  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit(form: NgForm) {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs correctement.';
      return;
    }

    const { pseudo, email, password, confirmPassword } = form.value;

    // Log des données avant envoi
    console.log('Données envoyées :', { pseudo, email, password, confirmPassword });

    if (password !== confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas.';
      return;
    }

    this.loading = true;

    this.authService.register(pseudo, email, password).subscribe({
      next: () => {
        this.successMessage = 'Compte créé avec succès ! Redirection en cours...';
        this.errorMessage = '';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err: any) => {
        console.error('Erreur inscription:', err);
        if (err.status === 0) {
          this.errorMessage = "Serveur indisponible, réessayez plus tard.";
        } else if (err.error?.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Erreur lors de la création du compte.';
        }
        this.successMessage = '';
      },
      complete: () => {
        this.loading = false;
      }
    });
  }


}
