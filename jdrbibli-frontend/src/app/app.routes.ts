import { Routes } from '@angular/router';

import { AuthGuard } from './guards/auth.guard'; // import du guard




export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/home-public/home-public').then(m => m.HomePublic) },
  { path: 'home-public', loadComponent: () => import('./pages/home-public/home-public').then(m => m.HomePublic) },
  { path: 'login', loadComponent: () => import('./pages/auth/login-page/login-page').then(m => m.LoginPage) },
  { path: 'success-login', loadComponent: () => import('./pages/success-login-page/success-login-page').then(m => m.SuccessLoginPage) },
  { path: 'register', loadComponent: () => import('./pages/auth/register-page/register-page').then(m => m.RegisterPageComponent) },
  { path: 'password-reset', loadComponent: () => import('./pages/auth/reset-password-request/reset-password-request').then(m => m.ResetPasswordRequestComponent) },
  {
    path: 'reset-password-code',
    loadComponent: () => import('./pages/auth/reset-password-code-page/reset-password-code-page').then(m => m.ResetPasswordCodePage)
  },
  {
    path: 'reset-password-newpass',
    loadComponent: () => import('./pages/auth/reset-password-newpass-page/reset-password-newpass-page').then(m => m.ResetPasswordNewpassPage)
  },
  { path: 'home-connected', loadComponent: () => import('./pages/home-connected/home-connected').then(m => m.HomeConnected), canActivate: [AuthGuard] },
  { path: 'ludotheque', loadComponent: () => import('./pages/ludotheque-page/ludotheque-page').then(m => m.LudothequePage) },
  { path: 'profil-utilisateur', loadComponent: () => import('./pages/profil-utilisateur/profil-utilisateur').then(m => m.ProfilUtilisateur) },
  { path: 'profile-edit', loadComponent: () => import('./pages/profil-utilisateur/profile-edit.component').then(m => m.ProfileEditComponent) },
  { path: 'gamme/:id', loadComponent: () => import('./pages/gamme-page/gamme-page').then(m => m.GammePage) },
  { path: 'ouvrage/:id', loadComponent: () => import('./pages/ouvrage-page/ouvrage-page').then(m => m.OuvragePage) },
  { path: 'ouvrage-detail/:id', loadComponent: () => import('./pages/ouvrage-detail-page/ouvrage-detail-page').then(m => m.OuvrageDetailPage) },
  { path: 'creation-edition/:id', loadComponent: () => import('./pages/creation-edition-page/creation-edition-page').then(m => m.CreationEditionPage), canActivate: [AuthGuard] },
  { path: 'creation-edition', loadComponent: () => import('./pages/creation-edition-page/creation-edition-page').then(m => m.CreationEditionPage), canActivate: [AuthGuard] },
  {
    path: 'reset-profil-password',
    loadComponent: () => import('./pages/auth/reset-profil-password-page/reset-profil-password-page').then(m => m.ResetProfilPasswordPage)
  },

  {
    path: 'ludotheque-ami/:friendId',
    loadComponent: () => import('./pages/ludotheque-ami-page/ludotheque-ami-page').then(m => m.LudothequeAmiPageComponent),
    canActivate: [AuthGuard]
  },


  {
    path: 'ouvrage-ami/:friendId/gamme/:gammeId',
    loadComponent: () => import('./pages/ouvrage-ami-page/ouvrage-ami-page').then(m => m.OuvrageAmiPageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'ouvrage-ami/:friendId/gamme/:gammeId/ouvrage/:ouvrageId',
    loadComponent: () => import('./pages/ouvrage-ami-detail-page/ouvrage-ami-detail-page').then(m => m.OuvrageAmiDetailPage),
    canActivate: [AuthGuard]
  },
  { path: '**', redirectTo: 'login' },
]

