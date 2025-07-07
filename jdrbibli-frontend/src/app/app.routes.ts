import { Routes } from '@angular/router';
import { HomePublic } from './home-public/home-public';
import { LoginPage } from './login-page/login-page';
import { HomeConnected } from './home-connected/home-connected';
import { RegisterPage } from './register-page/register-page';
import { ResetPasswordPage } from './reset-password-page/reset-password-page';
import { SuccessLoginPage } from './success-login-page/success-login-page';
import { authGuard } from './guards/auth.guard'; // importe le guard

export const routes: Routes = [
  { path: '', component: HomePublic },
  { path: 'login', component: LoginPage },
  { path: 'success-login', component: SuccessLoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'reset-password', component: ResetPasswordPage },
  { path: 'home', component: HomeConnected, canActivate: [authGuard]}
];
