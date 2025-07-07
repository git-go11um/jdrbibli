import { Component } from '@angular/core';

@Component({
  selector: 'app-success-login-page',
  standalone: true,
  template: `
    <div class="container">
      <h1>Connexion réussie ! 🎉</h1>
      <p>Bienvenue sur JdrBibli, vous êtes maintenant connecté.</p>
    </div>
  `,
  styles: [`
    .container {
      text-align: center;
      margin-top: 100px;
    }
  `]
})
export class SuccessLoginPage {}
