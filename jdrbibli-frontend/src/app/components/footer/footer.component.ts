import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `
    <footer class="app-footer">
      <p>© 2025 JdrBibli. Tous droits réservés.</p>
    </footer>
  `,
  styles: [`
    .app-footer {
      background-color: #3f51b5;
      color: white;
      padding: 1rem;
      text-align: center;
      position: fixed;
      bottom: 0;
      width: 100%;
      font-size: 0.9rem;
      box-shadow: 0 -2px 5px rgba(0,0,0,0.2);
    }
  `]
})
export class FooterComponent {}
