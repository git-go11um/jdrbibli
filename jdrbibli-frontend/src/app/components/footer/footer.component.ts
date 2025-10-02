import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `
    <footer class="app-footer">
      <div class="center">© 2025 JdrBibli</div>
      <div class="right">admin&#64;JDRBibli.fr</div>
      <div class="left"></div>
    </footer>
  `,
  styles: [`
    .app-footer {
      position: fixed;
      bottom: 0;
      width: 100%;
      background-color: #3f51b5;
      color: white;
      padding: 0.2rem 0.5rem;   /* réduit la hauteur */
      font-size: 0.75rem;        /* texte plus compact */
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
      white-space: nowrap; /* évite les retours à la ligne */
    }
    .left { text-align: left; }
    .right { text-align: right; }

    /* Version mobile : stack vertical */
    @media (max-width: 600px) {
      .app-footer {
        flex-direction: column;
        text-align: center;
        font-size: 0.7rem;
        padding: 0.3rem;
      }
      .left, .center, .right {
        text-align: center;
        flex: unset;
      }
    }
  `]
})
export class FooterComponent {}
