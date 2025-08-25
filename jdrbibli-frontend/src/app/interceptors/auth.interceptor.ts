// src/app/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
    // Endpoints publics où on ne met pas le JWT
    const excludedUrls = [
        '/api/auth/login',
        '/api/auth/register',
        '/api/auth/password-reset/request',
        '/api/auth/password-reset/verify-code',
        '/api/auth/password-reset/confirm',
        '/api/auth/validate-reset-code',
        '/api/auth/password-reset/change',
        '/api/auth/reset-password'
    ];

    // Vérifie si l'URL contient un endpoint public
    const isExcluded = excludedUrls.some(url => req.url.includes(url));

    if (!isExcluded) {
        const token = localStorage.getItem('jwt');
        if (token) {
            const authReq = req.clone({
                headers: req.headers.set('Authorization', `Bearer ${token}`)
            });
            return next(authReq);
        }
    }

    return next(req);
};
