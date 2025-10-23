import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class FriendService {
    private apiUrl = `${environment.apiUrl}/friends`;

    constructor(private http: HttpClient) { }

    private authHeaders(): HttpHeaders {
        const token = localStorage.getItem('jwt');
        return token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : new HttpHeaders();
    }

    listFriends(userId: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}?userId=${userId}`, { headers: this.authHeaders() })
            .pipe(catchError(err => throwError(() => err)));
    }

    listReceivedRequests(userId: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/requests/received?userId=${userId}`, { headers: this.authHeaders() })
            .pipe(catchError(err => throwError(() => err)));
    }

    sendRequest(senderId: number, receiverId: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/request?senderId=${senderId}&receiverId=${receiverId}`, {}, { headers: this.authHeaders() })
            .pipe(catchError(err => throwError(() => err)));
    }

    acceptRequest(requestId: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/${requestId}/accept`, {}, { headers: this.authHeaders() })
            .pipe(catchError(err => throwError(() => err)));
    }

    rejectRequest(requestId: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/${requestId}/reject`, {}, { headers: this.authHeaders() })
            .pipe(catchError(err => throwError(() => err)));
    }

    removeFriend(userId: number, friendId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${friendId}?userId=${userId}`, { headers: this.authHeaders() })
            .pipe(catchError(err => throwError(() => err)));
    }
}
