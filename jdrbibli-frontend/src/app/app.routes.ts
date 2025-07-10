import { Routes } from '@angular/router';
import { HomePublicComponent } from './pages/home-public/home-public.component';
import { LoginPage } from './pages/auth/login-page/login-page';
import { HomeConnected } from './pages/home-connected/home-connected.component';  // <-- HomeConnected standalone
import { RegisterPage } from './pages/auth/register-page/register-page.component';
import { ResetPasswordRequestComponent } from './pages/auth/reset-password-request/reset-password-request.component';
import { ResetPasswordCodePage } from './pages/auth/reset-password-code-page/reset-password-code-page';
import { ResetPasswordNewpassPage } from './pages/auth/reset-password-newpass-page/reset-password-newpass-page';
import { SuccessLoginPage } from './pages/success-login-page/success-login-page';
import { authGuard } from './guards/auth.guard';
import { ProfilePageComponent } from './pages/profile-page/profile-page.component';
import { LudothequePageComponent } from './pages/ludotheque-page/ludotheque-page.component';

export const routes: Routes = [
  { path: '', component: HomePublicComponent },
  { path: 'home-public', component: HomePublicComponent },
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
  {
    path: 'home-connected',
    loadComponent: () => import('./pages/home-connected/home-connected.component').then(m => m.HomeConnected),  // <-- standalone import
    canActivate: [authGuard]
  },
  { path: 'profile', component: ProfilePageComponent },
  { path: 'ludotheque', component: LudothequePageComponent },
];
