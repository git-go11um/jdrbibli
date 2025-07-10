import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home-public',
  templateUrl: './home-public.component.html',
  styleUrls: ['./home-public.component.scss']
})
export class HomePublicComponent {
  constructor(private router: Router) {}

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
