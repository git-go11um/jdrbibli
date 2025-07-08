import { Routes } from '@angular/router';
import { HomePublic } from './pages/home-public/home-public';
import { LoginPage } from './pages/login-page/login-page';
import { HomeConnected } from './pages/home-connected/home-connected';
import { RegisterPage } from './pages/register-page/register-page.component';
import { ResetPasswordPage } from './pages/reset-password-page/reset-password-page';
import { SuccessLoginPage } from './pages/success-login-page/success-login-page';
import { authGuard } from './guards/auth.guard'; // importe le guard


export const routes: Routes = [
  { path: '', component: HomePublic },
  { path: 'home-public', component: HomePublic },
  { path: 'login', component: LoginPage },
  { path: 'success-login', component: SuccessLoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'reset-password', component: ResetPasswordPage },
  { path: 'home', component: HomeConnected, canActivate: [authGuard]},
];
