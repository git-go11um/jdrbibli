import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) { }

  canActivate(): boolean {
    const token = localStorage.getItem('jwt');
    const loggedIn = this.authService.isLoggedIn();
    console.log('[AuthGuard] canActivate appelé, token =', token, ', isLoggedIn =', loggedIn);

    if (loggedIn) {
      return true;
    } else {
      // On force un petit délai pour être sûr que le service a bien lu le localStorage
      setTimeout(() => this.router.navigate(['/login']), 0);
      return false;
    }
  }
}

