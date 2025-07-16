import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register-page.html',
  styleUrls: ['./register-page.scss']
})
export class RegisterPageComponent {
  errorMessage = '';
  successMessage = '';
  loading = false;

  // CGU
  acceptedCGU = false;
  showCGUModal = false;

  // RGPD
  acceptedRGPD = false;
  showRGPDModal = false;

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit(form: NgForm) {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs correctement.';
      return;
    }

    const { pseudo, email, password, confirmPassword } = form.value;

    console.log('Données envoyées :', { pseudo, email, password, confirmPassword });

    if (password !== confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas.';
      return;
    }

    if (!this.acceptedCGU) {
      this.errorMessage = 'Vous devez accepter les conditions générales d\'utilisation.';
      return;
    }

    if (!this.acceptedRGPD) {
      this.errorMessage = 'Vous devez accepter la charte de confidentialité.';
      return;
    }

    this.loading = true;

    this.authService.register(pseudo, email, password).subscribe({
      next: () => {
        this.successMessage = 'Compte créé avec succès ! Redirection en cours...';
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
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  // CGU modal handlers
  openCGU(event: Event) {
    event.preventDefault();
    this.showCGUModal = true;
  }
  closeCGU() {
    this.showCGUModal = false;
  }

  // RGPD modal handlers
  openRGPD(event: Event) {
    event.preventDefault();
    this.showRGPDModal = true;
  }
  closeRGPD() {
    this.showRGPDModal = false;
  }
}
