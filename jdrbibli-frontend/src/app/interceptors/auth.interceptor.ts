// src/app/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';


export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
    // Endpoints publics où on ne met pas le JWT
    const excludedUrls = [
        'http://localhost:8084/api/auth/login',
        'http://localhost:8084/api/auth/register',
        'http://localhost:8084/api/auth/password-reset/request',
        'http://localhost:8084/api/auth/password-reset/verify-code',
        'http://localhost:8084/api/auth/password-reset/confirm',
        'http://localhost:8084/api/auth/validate-reset-code',
        'http://localhost:8084/api/auth/password-reset/change',
        'http://localhost:8084/api/auth/reset-password'
    ];

    // Vérifie si l'URL contient un endpoint public
    const isExcluded = excludedUrls.some(url => req.url.includes(url));

    if (!isExcluded) {
        const token = localStorage.getItem('jwt');
        if (token) {
            const authReq = req.clone({
                headers: req.headers.set('Authorization', `Bearer ${token}`)
            });
            return next(authReq).pipe(
                catchError(err => {
                    if (err.status === 401) {
                        localStorage.removeItem('jwt');
                        window.location.href = '/login'; // simple redirection
                    }
                    return throwError(() => err);
                })
            );

        }
    }

    return next(req);
};
