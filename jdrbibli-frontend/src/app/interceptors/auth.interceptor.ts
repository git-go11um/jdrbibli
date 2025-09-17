import { HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
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

    const isExcluded = excludedUrls.some(url => req.url.includes(url));

    if (!isExcluded) {
        let headers = req.headers;

        // JWT
        const token = localStorage.getItem('jwt');
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
