import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `
    <footer class="app-footer">
      <div class="center">© 2025 JdrBibli. Tous droits réservés.</div>
      <div class="right">Nous contacter : admin&#64;JDRBibli.fr</div>
      <div class="left"></div> <!-- vide pour équilibrer -->
    </footer>
  `,
  styles: [`
    .app-footer {
      position: fixed;
      bottom: 0;
      width: 100%;
      background-color: #3f51b5;
      color: white;
      padding: 0.3rem 1rem;
      font-size: 0.9rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 -2px 5px rgba(0,0,0,0.2);
      z-index: 1000;
    }
    .left, .center, .right {
      flex: 1;
      text-align: center;
      user-select: none;
    }
    .left {
      text-align: left;
    }
    .right {
      text-align: right;
    }
  `]
})
export class FooterComponent {}
