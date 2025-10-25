import { environment } from '../environments/environment';
import { HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
    const excludedUrls = [
        `${environment.apiUrl}/auth/login`,  // Utilisation de environment.apiUrl
        `${environment.apiUrl}/auth/register`,
        `${environment.apiUrl}/auth/password-reset/request`,
        `${environment.apiUrl}/auth/password-reset/verify-code`,
        `${environment.apiUrl}/auth/password-reset/confirm`,
        `${environment.apiUrl}/auth/validate-reset-code`,
        `${environment.apiUrl}/auth/password-reset/change`,
        `${environment.apiUrl}/auth/reset-password`
    ];

    const isExcluded = excludedUrls.some(url => req.url.includes(url));

    if (!isExcluded) {
        let headers = req.headers;

        // JWT
        const token = localStorage.getItem('jwt');

        console.log('🔍 Interceptor - URL interceptée:', req.url);
        console.log('🔍 Interceptor - Token actuel:', token);

        if (token) {
            headers = headers.set('Authorization', `Bearer ${token}`);
        }

        // User ID
        const userId = (() => {
            if (!token) return null;
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                return payload?.id ?? null;
            } catch {
                return null;
            }
        })();

        if (userId) {
            headers = headers.set('X-User-Id', userId.toString());
        }

        const authReq = req.clone({ headers });

        return next(authReq).pipe(
            catchError(err => {
                if (err.status === 401) {
                    localStorage.removeItem('jwt');
                    window.location.href = '/login';
                }
                return throwError(() => err);
            })
        );
    }

    return next(req);
};
