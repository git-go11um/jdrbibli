import { Routes } from '@angular/router';
import { HomePublic } from './pages/home-public/home-public';
import { LoginPage } from './pages/auth/login-page/login-page';
import { HomeConnected } from './pages/home-connected/home-connected';
import { RegisterPage } from './pages/auth/register-page/register-page.component';
import { ResetPasswordRequestComponent } from './pages/auth/reset-password-request/reset-password-request.component';
import { ResetPasswordCodePage } from './pages/auth/reset-password-code-page/reset-password-code-page';
import { ResetPasswordNewpassPage } from './pages/auth/reset-password-newpass-page/reset-password-newpass-page';
import { SuccessLoginPage } from './pages/success-login-page/success-login-page';
import { authGuard } from './guards/auth.guard'; // importe le guard


export const routes: Routes = [
  { path: '', component: HomePublic },
  { path: 'home-public', component: HomePublic },
  { path: 'login', component: LoginPage },
  { path: 'success-login', component: SuccessLoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'password-reset', component: ResetPasswordRequestComponent },
  {
    path: 'reset-password-code',
    loadComponent: () => import('./pages/auth/reset-password-code-page/reset-password-code-page').then(m => m.ResetPasswordCodePage)
  },
  {
    path: 'reset-password-newpass',
    loadComponent: () => import('./pages/auth/reset-password-newpass-page/reset-password-newpass-page').then(m => m.ResetPasswordNewpassPage)
  },
  { path: 'home', component: HomeConnected, canActivate: [authGuard] },


];
