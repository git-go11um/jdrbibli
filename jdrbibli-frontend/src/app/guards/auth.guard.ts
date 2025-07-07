import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');
  if (token) {
    return true;
  } else {
    window.alert('Vous devez être connecté pour accéder à cette page.');
    return router.parseUrl('/login');  // <-- redirection propre
  }
};
